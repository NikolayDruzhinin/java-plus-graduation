package ru.practicum.main.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.category.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByNameAndId(String name, Long id);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.category.id = :categoryId")
    boolean hasEvents(@Param("categoryId") Long categoryId);

    boolean existsByNameIgnoreCase(String name);

    List<Category> findAllByIdIn(List<Long> ids);
}