package ru.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.RequestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RequestClientFallback implements RequestClient {
    @Override
    public Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds) {
        log.warn("Request service is unavailable. Fallback method called for eventIds: {}", eventIds);
        return Collections.emptyMap();
    }
}

