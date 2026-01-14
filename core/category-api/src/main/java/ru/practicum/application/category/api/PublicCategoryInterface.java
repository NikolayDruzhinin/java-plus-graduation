package ru.practicum.application.category.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.List;

public interface PublicCategoryInterface {
    @GetMapping("/categories")
    List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/categories/{catId}")
    CategoryDto getCategoryById(@PathVariable Long catId) throws NotFoundException;
}
