package ru.practicum.explore.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.dto.EndHitDto;
import ru.practicum.explore.dto.StatDto;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DiscoveryStatsClient extends StatsClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final DiscoveryClient discoveryClient;
    private final String statsServiceId;

    public DiscoveryStatsClient(DiscoveryClient discoveryClient, String statsServiceId, RestTemplateBuilder builder) {
        super("", builder); // URL will be resolved dynamically from Eureka
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;
    }

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
                    exception
            );
        }
    }

    private URI makeUri(String path) {
        ServiceInstance instance = getInstance();
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    public ResponseEntity<Object> save(EndHitDto hit) {
        URI uri = makeUri("/hit");
        return submit(uri.toString(), hit);
    }

    public List<StatDto> getStats(String start, String end,
                                  List<String> uris, boolean unique) {

        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );

        URI uri = makeUri("/stats?start={start}&end={end}&uris={uris}&unique={unique}");
        ResponseEntity<Object> resp = fetch(uri.toString(), params);

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            return MAPPER.convertValue(resp.getBody(), new TypeReference<>() {});
        }
        return Collections.emptyList();
    }
}
