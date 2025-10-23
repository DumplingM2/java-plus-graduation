package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.stats.HitRequest;
import ru.practicum.dto.stats.GetResponse;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-service", path = "/stats", fallback = ru.practicum.feign.fallback.StatsClientFallback.class)
public interface StatsClient {

    @PostMapping("/hit")
    void addHit(@RequestBody HitRequest hitRequest);

    @GetMapping
    List<GetResponse> getStatistics(@RequestParam LocalDateTime start,
                                   @RequestParam LocalDateTime end,
                                   @RequestParam List<String> uris,
                                   @RequestParam(defaultValue = "false") Boolean unique);
}