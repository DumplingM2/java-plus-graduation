package ru.practicum.explore.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.explore.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RequestDto toRequestDto(Request request) {
        if (request == null) return null;
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreatedDate())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> toRequestDtoList(List<Request> requests) {
        if (requests == null) return null;
        return requests.stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }
}
