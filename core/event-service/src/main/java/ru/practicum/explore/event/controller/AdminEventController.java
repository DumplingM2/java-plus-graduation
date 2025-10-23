package ru.practicum.explore.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.AdminPatchEventDto;
import ru.practicum.dto.event.ResponseEventDto;
import ru.practicum.explore.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    
    private final EventService eventService;

    @GetMapping
    public Collection<ResponseEventDto> getAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        log.info("Get all events with params");
        return eventService.findAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/{eventId}")
    public ResponseEventDto getEventById(@PathVariable Long eventId) {
        log.info("Get event by id {}", eventId);
        return eventService.getEventByIdForAdmin(eventId);
    }

    @PatchMapping("/{eventId}")
    public ResponseEventDto patchEventById(@Valid @RequestBody AdminPatchEventDto adminPatchEventDto,
                                          @PathVariable Long eventId) {
        log.info("Patch event by id {}", eventId);
        return eventService.changeEventByAdmin(eventId, adminPatchEventDto);
    }
}
