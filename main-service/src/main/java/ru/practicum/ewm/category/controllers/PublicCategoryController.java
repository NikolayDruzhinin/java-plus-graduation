package ru.practicum.ewm.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.PublicCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/categories")
public class PublicCategoryController {
    public final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        log.info("GET-запрос к эндпоинту: '/categories' на получение categories (from = {}, size = {}", from, size);
        List<CategoryDto> response = publicCategoryService.getAllCategories(from, size);
        log.info("Сформирован ответ Get /categories с телом: {}", response);
        return response;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoriesById(@PathVariable long catId) {
        log.info("GET-запрос к эндпоинту: '/categories/{catId}' на получение categories");
        CategoryDto response = publicCategoryService.getCategoryById(catId);
        log.info("Сформирован ответ Get /categories/{} с телом: {}", catId, response);
        return response;
    }
}
