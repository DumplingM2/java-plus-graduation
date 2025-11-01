package ru.practicum.explorewithme.event.presentation.pub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.api.client.event.dto.EventFullDto;
import ru.practicum.explorewithme.api.client.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.application.EventService;
import ru.practicum.explorewithme.event.application.params.PublicEventSearchParams;
import ru.practicum.ewm.stats.client.aop.LogStatsHit;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @Valid PublicEventSearchParams params,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size,
            @RequestHeader(name = "X-Real-IP", required = false) String ipAddress) {

        log.info(
            "Public: Received request to get events with params: text={}, categories={}, paid={},"
                + " rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
            params.getText(), params.getCategories(), params.getPaid(), params.getRangeStart(),
            params.getRangeEnd(), params.getOnlyAvailable(), params.getSort(), from, size);

        List<EventShortDto> events = eventService.getEventsPublic(params, from, size);
        log.info("Public: Found {} events", events.size());
        return events;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(
            @PathVariable @Positive Long eventId,
            @RequestHeader(name = "X-EWM-USER-ID", required = false) Long userId,
            @RequestHeader(name = "X-Real-IP", required = false) String ipAddress) {
        log.info("Public: Received request to get event with id={}, userId={}", eventId, userId);
        EventFullDto event = eventService.getEventByIdPublic(eventId, userId);
        log.info("Public: Found event: {}", event);
        return event;
    }

    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getRecommendations(
            @RequestHeader(name = "X-EWM-USER-ID") @Positive Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Public: Received request to get recommendations for user id={}, from={}, size={}",
                userId, from, size);
        List<EventShortDto> recommendations = eventService.getRecommendations(userId, size);
        log.info("Public: Found {} recommendations for user id={}", recommendations.size(), userId);
        return recommendations;
    }

    @PutMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeEvent(
            @PathVariable @Positive Long eventId,
            @RequestHeader(name = "X-EWM-USER-ID") @Positive Long userId) {
        log.info("Public: Received request to like event id={} by user id={}", eventId, userId);
        eventService.likeEvent(userId, eventId);
        log.info("Public: Successfully processed like for event id={} by user id={}", eventId, userId);
    }
}