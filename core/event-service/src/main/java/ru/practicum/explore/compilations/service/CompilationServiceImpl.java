package ru.practicum.explore.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.compilations.mapper.CompilationMapper;
import ru.practicum.explore.compilations.model.Compilation;
import ru.practicum.explore.compilations.repository.CompilationRepository;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationResponse createCompilation(CompilationDto compilationDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        
        // Добавляем события, если они указаны
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            compilation.setEvents(new java.util.HashSet<>(eventRepository.findAllByIdIn(compilationDto.getEvents())));
        }
        
        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationResponse(savedCompilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationResponse changeCompilation(long compId, CompilationDto compilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        
        if (compilationDto == null) {
            return compilationMapper.toCompilationResponse(compilation);
        }
        
        // Валидация title
        if (compilationDto.getTitle() != null) {
            if (compilationDto.getTitle().length() > 50) {
                throw new ru.practicum.exception.BadRequestException("Title must be no more than 50 characters");
            }
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        
        // Обновляем события, если они указаны
        if (compilationDto.getEvents() != null) {
            if (compilationDto.getEvents().isEmpty()) {
                compilation.setEvents(new java.util.HashSet<>());
            } else {
                compilation.setEvents(new java.util.HashSet<>(eventRepository.findAllByIdIn(compilationDto.getEvents())));
            }
        }
        
        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationResponse(savedCompilation);
    }

    @Override
    public Collection<CompilationResponse> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        if (from == null) from = 0;
        if (size == null) size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).getContent()
                    .stream()
                    .map(compilationMapper::toCompilationResponse)
                    .collect(Collectors.toList());
        } else {
            // if repository doesn't have a dedicated method, filter in memory
            return compilationRepository.findAll(pageable).getContent()
                    .stream()
                    .filter(c -> c.getPinned() != null && c.getPinned().equals(pinned))
                    .map(compilationMapper::toCompilationResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationResponse getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return compilationMapper.toCompilationResponse(compilation);
    }
}
