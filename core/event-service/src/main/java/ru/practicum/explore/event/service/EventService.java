package ru.practicum.explore.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.ResponseEventDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    EventFullDto getEventById(long eventId);
    
    ResponseEventDto getEventByIdForAdmin(long eventId);

    EventFullDto getEventByUser(long userId, long eventId);

    Collection<ResponseEventDto> getAllUserEvents(long userId, Integer from, Integer size);

    ResponseEventDto changeEvent(long userId, long eventId, ru.practicum.dto.event.PatchEventDto patchEventDto);

    ResponseEventDto createEvent(long userId, ru.practicum.dto.event.NewEventDto newEventDto);

    Collection<ResponseEventDto> findEventsByUser(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  Integer from,
                                                  Integer size);

    ResponseEventDto changeEventByAdmin(long eventId, ru.practicum.dto.event.AdminPatchEventDto adminPatchEventDto);

    Collection<ResponseEventDto> findEventsByAdmin(List<Long> users,
                                                   List<String> states,
                                                   List<Long> categories,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Integer from,
                                                   Integer size);

    Collection<ResponseEventDto> getUserEvents(long userId, int from, int size);

    ResponseEventDto getUserEventById(long userId, long eventId);

    Collection<ResponseEventDto> findEvents(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            Integer from,
                                            Integer size,
                                            HttpServletRequest request);

    ResponseEventDto getPublicEvent(long eventId, HttpServletRequest request);

    Collection<ResponseEventDto> findAdminEvents(List<Long> users,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size);
}

