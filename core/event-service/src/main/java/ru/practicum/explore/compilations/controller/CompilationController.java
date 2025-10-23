package ru.practicum.explore.compilations.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;
import ru.practicum.explore.compilations.service.CompilationService;

import java.net.URI;
import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService service;

    @GetMapping("/compilations")
    public Collection<CompilationResponse> getAll(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        return service.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationResponse getById(@PathVariable @Positive Long compId) {
        return service.getCompilation(compId);
    }

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationResponse> create(@RequestBody @Valid CompilationDto dto) {
        CompilationResponse created = service.createCompilation(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationResponse update(@PathVariable @Positive Long compId,
                                     @RequestBody(required = false) @Valid CompilationDto dto) {
        return service.changeCompilation(compId, dto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long compId) {
        service.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }
}

