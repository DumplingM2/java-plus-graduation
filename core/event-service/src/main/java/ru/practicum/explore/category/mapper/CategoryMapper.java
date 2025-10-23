package ru.practicum.explore.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoWithId;
import ru.practicum.explore.category.model.Category;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .name(category.getName())
                .build();
    }

    public CategoryDtoWithId toCategoryDtoWithId(Category category) {
        if (category == null) return null;
        return CategoryDtoWithId.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategory(CategoryDto dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}

