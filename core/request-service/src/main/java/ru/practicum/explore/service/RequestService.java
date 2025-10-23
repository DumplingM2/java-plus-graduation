package ru.practicum.explore.service;

import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RequestService {
    RequestDto createRequest(long userId, long eventId);

    Collection<RequestDto> getUserRequests(long userId);

    RequestDto cancelRequest(long userId, long requestId);

    Collection<RequestDto> getEventRequests(long userId, long eventId);

    RequestDto confirmRequest(long userId, long eventId, long requestId);

    RequestDto rejectRequest(long userId, long eventId, long requestId);

    Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds);

    EventRequestStatusUpdateResult updateRequestStatuses(long userId,
                                                         long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest);
}

