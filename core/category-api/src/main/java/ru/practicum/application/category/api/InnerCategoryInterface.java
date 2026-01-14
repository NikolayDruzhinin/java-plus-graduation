package ru.practicum.application.category.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.category.CategoryDto;

import java.util.List;
import java.util.Set;

public interface InnerCategoryInterface {
    @GetMapping("/inner/category/exist/{categoryId}")
    boolean existById(@PathVariable Long categoryId);

    @GetMapping("/inner/category/all")
    List<CategoryDto> getCategoriesByIds(@RequestParam Set<Long> ids);
}
