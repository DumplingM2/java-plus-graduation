package ru.practicum.explore.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoWithId;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDtoWithId createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Category with this name already exists");
        }
        Category category = categoryMapper.toCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDtoWithId(savedCategory);
    }

    @Override
    public CategoryDtoWithId changeCategory(long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        
        if (categoryRepository.existsByNameAndIdNot(categoryDto.getName(), catId)) {
            throw new ConflictException("Category with this name already exists");
        }
        
        category.setName(categoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDtoWithId(savedCategory);
    }

    @Override
    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        
        if (categoryRepository.countEventsByCategoryId(catId) > 0) {
            throw new ConflictException("Cannot delete category with events");
        }
        
        categoryRepository.deleteById(catId);
    }

    @Override
    public Collection<CategoryDtoWithId> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).getContent()
                .stream()
                .map(categoryMapper::toCategoryDtoWithId)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDtoWithId getCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return categoryMapper.toCategoryDtoWithId(category);
    }
}
