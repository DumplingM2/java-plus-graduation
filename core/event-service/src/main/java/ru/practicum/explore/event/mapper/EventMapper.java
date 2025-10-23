package ru.practicum.explore.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDtoWithId;
import ru.practicum.dto.event.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    /* ---------- helpers для вложенных сущностей ---------- */

    public static CategoryDtoWithId mapToCategoryDtoWithId(Category category) {
        if (category == null) return null;
        return CategoryDtoWithId.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }


    public static LocationDto mapToLocationDto(Location location) {
        if (location == null) return null;
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static UserDto createUserDtoFromId(Long userId) {
        if (userId == null) return null;
        return UserDto.builder()
                .id(userId)
                .name("User " + userId)
                .email("user" + userId + "@example.com")
                .build();
    }

    public static Location mapToLocation(LocationDto dto) {
        if (dto == null) return null;
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    public static EventDto mapToEventDto(Event event) {
        if (event == null) return null;
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory() != null ? event.getCategory().getId() : null)
                .confirmedRequests(event.getConfirmedRequests().longValue())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiatorId())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventDto> mapToEventDto(Iterable<Event> events) {
        List<EventDto> res = new ArrayList<>();
        for (Event e : events) res.add(mapToEventDto(e));
        return res;
    }

    public static ResponseEventDto mapToResponseEventDto(Event event) {
        if (event == null) return null;

        return ResponseEventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(mapToCategoryDtoWithId(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(createUserDtoFromId(event.getInitiatorId()))
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests().longValue())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(mapToLocationDto(event.getLocation()))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static List<ResponseEventDto> mapToResponseEventDto(Iterable<Event> events) {
        List<ResponseEventDto> res = new ArrayList<>();
        for (Event e : events) res.add(mapToResponseEventDto(e));
        return res;
    }

    public static Event toEntity(NewEventDto dto,
                                 Long initiatorId,
                                 Category category,
                                 Location location) {

        Event e = new Event();
        e.setTitle(dto.getTitle());
        e.setAnnotation(dto.getAnnotation());
        e.setDescription(dto.getDescription());
        e.setEventDate(dto.getEventDate());
        e.setCategory(category);
        e.setInitiatorId(initiatorId);
        e.setLocation(location);

        e.setPaid(dto.getPaid() != null && dto.getPaid());
        e.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        e.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());

        e.setState("PENDING");
        e.setCreatedOn(LocalDateTime.now());
        e.setViews(0L);
        e.setConfirmedRequests(0);

        return e;
    }

    /* ---------- entity → полный dto ---------- */

    public static EventFullDto toFullDto(Event e) {
        if (e == null) return null;
        return EventFullDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .category(mapToCategoryDtoWithId(e.getCategory()))
                .paid(e.getPaid())
                .eventDate(e.getEventDate())
                .initiator(createUserDtoFromId(e.getInitiatorId()))
                .views(e.getViews())
                .confirmedRequests(e.getConfirmedRequests().longValue())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .state(e.getState())
                .createdOn(e.getCreatedOn())
                .publishedOn(e.getPublishedOn())
                .location(mapToLocationDto(e.getLocation()))
                .build();
    }

    /* ---------- patch helper ---------- */

    public static Event changeEvent(Event event, PatchEventDto patch) {
        if (patch.getAnnotation() != null)          event.setAnnotation(patch.getAnnotation());
        if (patch.getDescription() != null)         event.setDescription(patch.getDescription());
        if (patch.getEventDate() != null)           event.setEventDate(patch.getEventDate());
        if (patch.getPaid() != null)                event.setPaid(patch.getPaid());
        if (patch.getParticipantLimit() != null &&
                patch.getParticipantLimit() >= 0)       event.setParticipantLimit(patch.getParticipantLimit());
        if (patch.getRequestModeration() != null)   event.setRequestModeration(patch.getRequestModeration());
        if (patch.getTitle() != null)               event.setTitle(patch.getTitle());
        if (patch.getLocation() != null)            event.setLocation(mapToLocation(patch.getLocation()));
        return event;
    }

    public static Event changeEvent(Event event, AdminPatchEventDto patch) {
        if (patch.getAnnotation() != null)          event.setAnnotation(patch.getAnnotation());
        if (patch.getDescription() != null)         event.setDescription(patch.getDescription());
        if (patch.getEventDate() != null)           event.setEventDate(patch.getEventDate());
        if (patch.getPaid() != null)                event.setPaid(patch.getPaid());
        if (patch.getParticipantLimit() != null &&
                patch.getParticipantLimit() >= 0)       event.setParticipantLimit(patch.getParticipantLimit());
        if (patch.getRequestModeration() != null)   event.setRequestModeration(patch.getRequestModeration());
        if (patch.getTitle() != null)               event.setTitle(patch.getTitle());
        if (patch.getLocation() != null)            event.setLocation(mapToLocation(patch.getLocation()));
        return event;
    }
}

