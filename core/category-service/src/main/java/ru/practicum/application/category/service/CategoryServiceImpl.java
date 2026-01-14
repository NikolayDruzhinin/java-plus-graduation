package ru.practicum.application.category.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.category.mapper.CategoryMapper;
import ru.practicum.application.category.model.Category;
import ru.practicum.application.category.repository.CategoryRepository;
import ru.practicum.application.event.client.EventClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {

    final CategoryRepository categoryRepository;
    final EventClient eventClient;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) throws ConflictException {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Такая категория событий уже существует");
        }
        var category = categoryRepository.save(CategoryMapper.mapCategoryDto(categoryDto));
        return CategoryMapper.mapCategory(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException, ConflictException {
        // Найти категорию по ID, либо выбросить исключение, если не найдена
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена."));

        // Проверить, существует ли другая категория с таким же именем
        boolean categoryExists = categoryRepository.findByName(categoryDto.getName()).stream()
                .anyMatch(c -> !c.getId().equals(catId));

        if (categoryExists) {
            throw new ConflictException(String.format(
                    "Нельзя задать имя категории %s, поскольку такое имя уже используется.",
                    categoryDto.getName()
            ));
        }

        // Обновить и сохранить изменения
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.mapCategory(updatedCategory);
    }


    @Override
    public CategoryDto getCategoryById(Long catId) throws NotFoundException {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена " + catId));

        return CategoryMapper.mapCategory(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .getContent()
                .stream()
                .map(CategoryMapper::mapCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) throws ConflictException, NotFoundException {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id=%d не существует", catId));
        }

        if (!eventClient.existsByCategoryId(catId)) {
            categoryRepository.deleteById(catId);
        } else {
            throw new ConflictException("Невозможно удаление используемой категории события ");
        }
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(Set<Long> ids) {
        return categoryRepository.findAllById(ids).stream().map(CategoryMapper::mapCategory).collect(Collectors.toList());
    }

    @Override
    public boolean existById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
