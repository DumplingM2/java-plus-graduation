package ru.practicum.explore.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoWithId;
import ru.practicum.explore.category.service.CategoryService;

import java.net.URI;
import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/categories")
    public Collection<CategoryDtoWithId> getAll(
            @RequestParam(defaultValue = "0")  @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive       Integer size) {

        return service.getAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDtoWithId getById(@PathVariable @Positive Long catId) {
        return service.getCategory(catId);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDtoWithId> add(@RequestBody @Valid CategoryDto dto) {
        CategoryDtoWithId saved = service.createCategory(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDtoWithId update(@PathVariable @Positive Long catId,
                                    @RequestBody @Valid CategoryDto dto) {
        return service.changeCategory(catId, dto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long catId) {
        service.deleteCategory(catId);
        return ResponseEntity.noContent().build();
    }
}
