package ru.practicum.explore.event.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.feign.EventClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/feign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFeignController implements EventClient {
    final EventService eventService;

    @Override
    public EventFullDto getEventByIdFeign(Long eventId) {
        log.info("Feign request for event with id = {}", eventId);
        return eventService.getEventById(eventId);
    }

    @Override
    public EventFullDto getEventByUserFeign(Long userId, Long eventId) {
        log.info("Feign request for event with id = {} and user with id = {}", eventId, userId);
        return eventService.getEventById(eventId);
    }
}
