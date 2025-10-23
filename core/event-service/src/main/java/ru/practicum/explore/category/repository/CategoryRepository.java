package ru.practicum.explore.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.category.id = :categoryId")
    long countEventsByCategoryId(Long categoryId);
}

