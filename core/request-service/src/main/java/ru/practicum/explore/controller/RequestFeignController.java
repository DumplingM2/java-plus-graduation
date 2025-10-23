package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.explore.service.RequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/requests/feign")
@RequiredArgsConstructor
public class RequestFeignController {

    private final RequestService requestService;

    @GetMapping("/confirmed")
    public Map<Long, List<RequestDto>> getConfirmedRequests(@RequestParam List<Long> eventIds) {
        log.info("Feign request to get confirmed requests for eventIds: {}", eventIds);
        return requestService.getConfirmedRequests(eventIds);
    }
}

