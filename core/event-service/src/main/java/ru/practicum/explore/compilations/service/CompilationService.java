package ru.practicum.explore.compilations.service;

import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;

import java.util.Collection;

public interface CompilationService {
    CompilationResponse createCompilation(CompilationDto dto);

    CompilationResponse changeCompilation(long compId, CompilationDto dto);

    void deleteCompilation(long compId);

    Collection<CompilationResponse> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationResponse getCompilation(long compId);
}

