package ru.practicum.explorewithme.request.presentation.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.api.client.request.RequestClient;
import ru.practicum.explorewithme.api.client.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.application.RequestService;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InternalRequestController implements RequestClient {

    private final RequestService requestService;

    @Override
    @GetMapping("/confirmed-counts")
    public Map<Long, Long> getConfirmedRequestCounts(Set<Long> eventIds) {
        return requestService.getConfirmedRequestCounts(eventIds);
    }

    @Override
    @GetMapping("/users/{userId}")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId) {
        return requestService.getRequests(userId);
    }
}
