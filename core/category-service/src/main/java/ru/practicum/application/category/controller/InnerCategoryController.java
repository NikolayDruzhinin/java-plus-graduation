package ru.practicum.application.category.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.category.api.InnerCategoryInterface;
import ru.practicum.application.category.service.CategoryService;

import java.util.List;
import java.util.Set;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InnerCategoryController implements InnerCategoryInterface {

    final CategoryService categoryService;

    @Override
    public boolean existById(Long categoryId) {
        return categoryService.existById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(Set<Long> ids) {
        return categoryService.getCategoriesByIds(ids);
    }
}
