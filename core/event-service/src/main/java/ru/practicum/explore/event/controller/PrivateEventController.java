package ru.practicum.explore.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.PatchEventDto;
import ru.practicum.dto.event.ResponseEventDto;
import ru.practicum.explore.event.service.EventService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    
    private final EventService eventService;

    @GetMapping
    public Collection<ResponseEventDto> getAllEvents(@PathVariable Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                                    @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get all events for user with id = {}", userId);
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEventDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Create event for user with id = {}", userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public ResponseEventDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get event with id = {} for user with id = {}", eventId, userId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public ResponseEventDto updateEvent(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @Valid @RequestBody PatchEventDto updateRequest) {
        log.info("Update event with id = {} for user with id = {}", eventId, userId);
        return eventService.changeEvent(userId, eventId, updateRequest);
    }
}
