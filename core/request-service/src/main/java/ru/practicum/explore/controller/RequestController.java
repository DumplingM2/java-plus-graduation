package ru.practicum.explore.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.explore.service.RequestService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable @Positive Long userId,
                                    @RequestParam @Positive Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public Collection<RequestDto> getUserRequests(@PathVariable @Positive Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public Collection<RequestDto> getEventRequests(@PathVariable @Positive Long userId,
                                                   @PathVariable @Positive Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{requestId}/confirm")
    public RequestDto confirmRequest(@PathVariable @Positive Long userId,
                                     @PathVariable @Positive Long eventId,
                                     @PathVariable @Positive Long requestId) {
        return requestService.confirmRequest(userId, eventId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests/{requestId}/reject")
    public RequestDto rejectRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @PathVariable @Positive Long requestId) {
        return requestService.rejectRequest(userId, eventId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatuses(@PathVariable @Positive Long userId,
                                                        @PathVariable @Positive Long eventId,
                                                        @RequestBody(required = false) EventRequestStatusUpdateRequest updateRequest) {
        return requestService.updateRequestStatuses(userId, eventId, updateRequest);
    }
}

