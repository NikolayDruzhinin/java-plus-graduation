package ru.practicum.ewm.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.AdminCategoryService;
import ru.practicum.ewm.validation.CreateGroup;
import ru.practicum.ewm.validation.UpdateGroup;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    public final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Validated(CreateGroup.class) @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Поступил запрос Post /admin/categories на создание Category с телом {}", newCategoryDto);
        CategoryDto response = adminCategoryService.createCategory(newCategoryDto);
        log.info("Сформирован ответ Post /admin/categories с телом: {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@Validated(UpdateGroup.class) @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long id) {
        log.info("Поступил запрос Patch /admin/categories/{} на обновление Category с телом {}", id, categoryDto);
        CategoryDto response = adminCategoryService.updateCategory(categoryDto, id);
        log.info("Сформирован ответ Patch /admin/categories/{} с телом: {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Поступил запрос Delete /admin/categories/{} на удаление Category с id {}", id, id);
        adminCategoryService.deleteCategoryById(id);
        log.info("Выполнен запрос Delete /admin/categories/{} на удаление Category с id {}", id, id);
    }


}
