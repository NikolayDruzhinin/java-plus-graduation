package ru.practicum.main.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.entity.Category;

@UtilityClass
public class CategoryMapper {

    public static Category toEntity(NewCategoryDto dto) {
        return new Category(dto.getName());
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category updateFromDto(CategoryDto dto, Category category) {
        category.setName(dto.getName());
        return category;
    }
}