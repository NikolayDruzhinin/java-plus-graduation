package ru.practicum.main.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Admin Создал категорию: {}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") @Positive Long categoryId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Admin: Обновил категорию с id: {}", categoryId);
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") @Positive Long categoryId) {
        log.info("Admin: Удалил категорию с id: {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}