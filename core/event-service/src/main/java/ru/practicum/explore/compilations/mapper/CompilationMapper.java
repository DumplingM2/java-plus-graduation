package ru.practicum.explore.compilations.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;
import ru.practicum.dto.event.EventDto;
import ru.practicum.explore.compilations.model.Compilation;
import ru.practicum.explore.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) return null;
        
        List<Long> eventIds = null;
        if (compilation.getEvents() != null) {
            eventIds = compilation.getEvents().stream()
                    .map(event -> event.getId())
                    .collect(Collectors.toList());
        }
        
        return CompilationDto.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventIds)
                .build();
    }

    public CompilationResponse toCompilationResponse(Compilation compilation) {
        if (compilation == null) return null;
        
        List<EventDto> events = new ArrayList<>();
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            events = compilation.getEvents().stream()
                    .map(event -> EventMapper.mapToEventDto(event))
                    .collect(Collectors.toList());
        }
        
        return CompilationResponse.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }

    public Compilation toCompilation(CompilationDto dto) {
        if (dto == null) return null;
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        // События будут добавлены в сервисе
        return compilation;
    }
}
