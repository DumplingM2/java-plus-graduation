package ru.practicum.explore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explore.client.DiscoveryStatsClient;

@Configuration
public class StatsClientConfig {

    @Value("${stats-service.id:stats-service}")
    private String statsServiceId;

    @Bean
    public DiscoveryStatsClient statsClient(DiscoveryClient discoveryClient, RestTemplateBuilder builder) {
        return new DiscoveryStatsClient(discoveryClient, statsServiceId, builder);
    }
}
