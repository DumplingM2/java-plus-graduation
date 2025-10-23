package ru.practicum.explore.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.ResponseEventDto;
import ru.practicum.explore.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    
    private final EventService eventService;

    @GetMapping
    public Collection<ResponseEventDto> getAllEventsPublic(
            @RequestParam(required = false) @Size(min = 1, max = 7000, message = "Description should be between 1 and 7000 characters long") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
            HttpServletRequest httpServletRequest) {
        log.info("Get all public events");
        return eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest);
    }

    @GetMapping("/{eventId}")
    public ResponseEventDto getEventByIdPublic(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        log.info("Get public event with id: {}", eventId);
        return eventService.getPublicEvent(eventId, httpServletRequest);
    }
}
