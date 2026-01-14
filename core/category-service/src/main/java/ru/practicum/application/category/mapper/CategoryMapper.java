package ru.practicum.application.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category mapCategoryDto(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public CategoryDto mapCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

}
