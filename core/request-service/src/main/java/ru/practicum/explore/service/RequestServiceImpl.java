package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.Request;
import ru.practicum.explore.repository.RequestRepository;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.enums.RequestStatus;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateRequestException;
import ru.practicum.exception.NotPublishedEventRequestException;
import ru.practicum.exception.RequestLimitException;
import ru.practicum.exception.InitiatorRequestException;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.TooManyRequestsException;
import ru.practicum.exception.AlreadyConfirmedException;
import ru.practicum.feign.EventClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.EventState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventClient eventClient;

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        // Проверяем, не существует ли уже запрос от этого пользователя на это событие
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DuplicateRequestException("Request already exists");
        }
        
        // Получаем информацию о событии
        ru.practicum.dto.event.EventFullDto event = eventClient.getEventByIdFeign(eventId);
        
        // Проверяем, что событие опубликовано
        if (!event.getState().equals(EventState.PUBLISHED.name())) {
            throw new NotPublishedEventRequestException("Event must be published");
        }
        
        // Проверяем, что пользователь не является инициатором события
        if (event.getInitiator().getId().equals(userId)) {
            throw new InitiatorRequestException("Initiator can't submit a request for event");
        }
        
        // Проверяем лимит участников
        int currentRequests = requestRepository.findByEventId(eventId).size();
        if (event.getParticipantLimit() != 0 && currentRequests >= event.getParticipantLimit()) {
            throw new RequestLimitException("No more seats for the event");
        }
        
        // Определяем статус запроса
        String status = "PENDING";
        if (event.getParticipantLimit() == 0) {
            status = "CONFIRMED";
        }
        
        Request request = new Request();
        request.setRequesterId(userId);
        request.setEventId(eventId);
        request.setStatus(status);
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public Collection<RequestDto> getUserRequests(long userId) {
        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        
        if ("CANCELED".equals(request.getStatus())) {
            throw new ConflictException("Request is already canceled");
        }
        
        request.setStatus("CANCELED");
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public Collection<RequestDto> getEventRequests(long userId, long eventId) {
        return requestRepository.findByEventId(eventId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto confirmRequest(long userId, long eventId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("CONFIRMED");
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto rejectRequest(long userId, long eventId, long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("REJECTED");
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds) {
        Map<Long, List<RequestDto>> res = new HashMap<>();
        for (Long id : eventIds) {
            Collection<Request> requestsCollection = requestRepository.findByEventId(id);
            List<Request> requestsForEvent = new ArrayList<>(requestsCollection);
            List<RequestDto> dtos = requestsForEvent.stream()
                    .filter(r -> "CONFIRMED".equals(r.getStatus()))
                    .map(r -> requestMapper.toRequestDto(r))
                    .collect(Collectors.toList());
            res.put(id, dtos);
        }
        return res;
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatuses(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {
        // Проверяем, что updateRequest не null
        if (updateRequest == null) {
            throw new BadRequestException("Update request cannot be null");
        }
        
        // Получаем событие для проверки лимита участников
        EventFullDto event = eventClient.getEventByUserFeign(userId, eventId);
        
        List<Request> requests = requestRepository.findByIdInAndEventId(updateRequest.getRequestIds(), eventId)
                .stream().collect(Collectors.toList());

        // Проверяем, что все запросы принадлежат данному событию
        for (Request request : requests) {
            if (!request.getEventId().equals(eventId)) {
                throw new NotFoundException("Request with requestId = " + request.getId() + " does not match eventId = " + eventId);
            }
        }

        // Проверяем лимит участников
        int confirmedCount = requestRepository.findByEventIdAndStatus(eventId, "CONFIRMED").size();
        int size = updateRequest.getRequestIds().size();
        int confirmedSize = updateRequest.getStatus().equals(RequestStatus.CONFIRMED) ? size : 0;

        if (event.getParticipantLimit() != 0 && confirmedCount + confirmedSize > event.getParticipantLimit()) {
            throw new TooManyRequestsException("Event limit exceed");
        }

        List<RequestDto> confirmed = new ArrayList<>();
        List<RequestDto> rejected = new ArrayList<>();

        for (Request r : requests) {
            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                r.setStatus("CONFIRMED");
                confirmed.add(requestMapper.toRequestDto(requestRepository.save(r)));
            } else if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                if ("CONFIRMED".equals(r.getStatus())) {
                    throw new AlreadyConfirmedException("The request cannot be rejected if it is confirmed");
                }
                r.setStatus("REJECTED");
                rejected.add(requestMapper.toRequestDto(requestRepository.save(r)));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }
}
