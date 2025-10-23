package ru.practicum.explore.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.PatchEventDto;
import ru.practicum.dto.event.ResponseEventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.explore.category.model.Category;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.Location;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.event.repository.LocationRepository;
import ru.practicum.feign.UserClient;
import ru.practicum.feign.StatsClient;
import ru.practicum.dto.stats.HitRequest;
import ru.practicum.dto.stats.GetResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserClient userClient;
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public ResponseEventDto createEvent(long userId, NewEventDto newEventDto) {
        // Check if user exists using Feign client
        try {
            userClient.getUser(userId);
        } catch (Exception e) {
            throw new NotFoundException("User not found");
        }
        
        if (newEventDto.getEventDate() == null || newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours in the future");
        }
        
        // Валидация participantLimit
        if (newEventDto.getParticipantLimit() != null && newEventDto.getParticipantLimit() < 0) {
            throw new BadRequestException("Participant limit cannot be negative");
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Location location = null;
        if (newEventDto.getLocation() != null) {
            location = EventMapper.mapToLocation(newEventDto.getLocation());
            location = locationRepository.save(location);
        }
        
        Event event = EventMapper.toEntity(newEventDto, userId, category, location);

        Event saved = eventRepository.save(event);
        return EventMapper.mapToResponseEventDto(saved);
    }

    @Override
    public Collection<ResponseEventDto> getAllUserEvents(long userId, Integer from, Integer size) {
        if (from == null) from = 0;
        if (size == null) size = 10;
        Pageable pageable = PageRequest.of(from, size);
        return EventMapper.mapToResponseEventDto(eventRepository.findByInitiatorId(userId, pageable).getContent());
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event not found"));
        return EventMapper.toFullDto(event);
    }

    @Override
    public ResponseEventDto changeEvent(long userId, long eventId, PatchEventDto patchEventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event not found"));
        
        if ("PUBLISHED".equals(event.getState())) {
            throw new ConflictException("Cannot change published event");
        }
        
        
        Event updated = EventMapper.changeEvent(event, patchEventDto);
        // При изменении события пользователем, оно может перейти в статус PENDING или CANCELED
        if (patchEventDto.getStateAction() != null) {
            if ("CANCEL_REVIEW".equals(patchEventDto.getStateAction())) {
                updated.setState("CANCELED");
            } else if ("SEND_TO_REVIEW".equals(patchEventDto.getStateAction())) {
                updated.setState("PENDING");
            }
        }
        return EventMapper.mapToResponseEventDto(eventRepository.save(updated));
    }

    @Override
    public Collection<ResponseEventDto> findEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (from == null) from = 0;
        if (size == null) size = 10;
        Pageable pageable = PageRequest.of(from, size);
        List<Event> result;
        if (users != null && !users.isEmpty() && states != null && !states.isEmpty() && categories != null && !categories.isEmpty()) {
            result = eventRepository.findUsersStatesCategories(users, states, categories, rangeStart, rangeEnd, pageable);
        } else if (users != null && !users.isEmpty() && states != null && !states.isEmpty()) {
            result = eventRepository.findUsersStates(users, states, rangeStart, rangeEnd, pageable);
        } else if (users != null && !users.isEmpty() && categories != null && !categories.isEmpty()) {
            result = eventRepository.findUsersCategories(users, categories, rangeStart, rangeEnd, pageable);
        } else if (states != null && !states.isEmpty() && categories != null && !categories.isEmpty()) {
            result = eventRepository.findStatesCategories(states, categories, rangeStart, rangeEnd, pageable);
        } else if (users != null && !users.isEmpty()) {
            result = eventRepository.findUsersEvents(users, rangeStart, rangeEnd, pageable);
        } else if (states != null && !states.isEmpty()) {
            result = eventRepository.findStates(states, rangeStart, rangeEnd, pageable);
        } else if (categories != null && !categories.isEmpty()) {
            result = eventRepository.findCategories(categories, rangeStart, rangeEnd, pageable);
        } else {
            result = eventRepository.findByDateRange(rangeStart, rangeEnd, pageable);
        }
        
        return EventMapper.mapToResponseEventDto(result);
    }

    @Override
    public ResponseEventDto changeEventByAdmin(long eventId, ru.practicum.dto.event.AdminPatchEventDto adminPatchEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        
        // Обновляем поля события, если они указаны
        EventMapper.changeEvent(event, adminPatchEventDto);
        
        // Обработка stateAction
        if (adminPatchEventDto.getStateAction() != null) {
            String stateAction = adminPatchEventDto.getStateAction().name();
            String currentState = event.getState();
            
            if ("PUBLISH_EVENT".equals(stateAction)) {
                if (!"PENDING".equals(currentState)) {
                    throw new ConflictException("The event can only be published during the pending stage");
                }
                event.setState("PUBLISHED");
                event.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equals(stateAction)) {
                if ("PUBLISHED".equals(currentState)) {
                    throw new ConflictException("Cannot reject a published event");
                }
                event.setState("CANCELED");
            }
        }
        
        return EventMapper.mapToResponseEventDto(eventRepository.save(event));
    }

    @Override
    public Collection<ResponseEventDto> findEventsByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        if (from == null) from = 0;
        if (size == null) size = 10;
        Pageable pageable = PageRequest.of(from, size);
        List<Event> base = eventRepository.findByDateRange(rangeStart, rangeEnd, pageable);
        return base.stream()
                .filter(e -> text == null || (e.getAnnotation() != null && e.getAnnotation().toLowerCase().contains(text.toLowerCase())) || (e.getDescription() != null && e.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(e -> categories == null || categories.isEmpty() || (e.getCategory() != null && categories.contains(e.getCategory().getId())))
                .filter(e -> paid == null || (e.getPaid() != null && e.getPaid().equals(paid)))
                .map(EventMapper::mapToResponseEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseEventDto> getUserEvents(long userId, int from, int size) {
        return getAllUserEvents(userId, from, size);
    }

    @Override
    public ResponseEventDto getUserEventById(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return EventMapper.mapToResponseEventDto(event);
    }

    @Override
    public Collection<ResponseEventDto> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        return findEventsByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @Override
    public ResponseEventDto getPublicEvent(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, "PUBLISHED").orElseThrow(() -> new NotFoundException("Event not found"));
        
        // Сохраняем просмотр в статистике
        saveViewInStatistic("/events/" + eventId, request.getRemoteAddr());
        addViewsInEvent(event);
        
        return EventMapper.mapToResponseEventDto(event);
    }

    @Override
    public Collection<ResponseEventDto> findAdminEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    public EventFullDto getEventById(long eventId) {
        Event e = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        return EventMapper.toFullDto(e);
    }
    
    @Override
    public ResponseEventDto getEventByIdForAdmin(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return EventMapper.mapToResponseEventDto(event);
    }

    private void saveViewInStatistic(String uri, String ip) {
        HitRequest hitRequest = HitRequest.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .build();
        try {
            statsClient.addHit(hitRequest);
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to save view in statistics: " + e.getMessage());
        }
    }

    private void addViewsInEvent(Event event) {
        if (event.getPublishedOn() == null) {
            return;
        }
        
        try {
            List<GetResponse> getResponses = statsClient.getStatistics(
                    event.getPublishedOn(),
                    LocalDateTime.now(),
                    List.of("/events/" + event.getId()),
                    true);

            if (!getResponses.isEmpty()) {
                GetResponse getResponse = getResponses.get(0);
                event.setViews(getResponse.getHits());
            }
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to load views from statistics: " + e.getMessage());
        }
    }
}
