package ru.practicum.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndHitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}

