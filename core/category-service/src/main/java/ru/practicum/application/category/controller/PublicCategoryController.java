package ru.practicum.application.category.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.category.api.PublicCategoryInterface;
import ru.practicum.application.category.service.CategoryService;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PublicCategoryController implements PublicCategoryInterface {
    final CategoryService categoryService;

    @Override
    public List<CategoryDto> getAllCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        return categoryService.getAllCategories(from, size);
    }

    @Override
    public CategoryDto getCategoryById(@PathVariable Long catId) throws NotFoundException {
        return categoryService.getCategoryById(catId);
    }
}
