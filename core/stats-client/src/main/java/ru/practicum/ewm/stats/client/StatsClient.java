package ru.practicum.ewm.stats.client;

import ru.practicum.ewm.stats.dto.ViewStatsDto;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

