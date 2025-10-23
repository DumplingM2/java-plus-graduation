package ru.practicum.explore.category.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoWithId;

import java.util.Collection;

public interface CategoryService {
    CategoryDtoWithId createCategory(CategoryDto dto);

    CategoryDtoWithId changeCategory(long catId, CategoryDto dto);

    void deleteCategory(long catId);

    Collection<CategoryDtoWithId> getAllCategories(Integer from, Integer size);

    CategoryDtoWithId getCategory(long catId);
}

