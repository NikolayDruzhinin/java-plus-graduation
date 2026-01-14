package ru.practicum.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto dto);
    CategoryDto update(Long catId, NewCategoryDto dto);
    void delete(Long catId);

    List<CategoryDto> getAll(int from, int size);
    CategoryDto getById(Long catId);
}
