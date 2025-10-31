package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
public class StatsClientImpl implements StatsClient {

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.warn("StatsClient.getStats() called but not implemented yet. Returning empty list.");
        // TODO: Implement statistics retrieval via gRPC or HTTP
        return Collections.emptyList();
    }
}

