package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.entity.Category;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Создана новая категория: {}", newCategoryDto);

        if (categoryRepository.findByName(newCategoryDto.getName()).isPresent()) {
            throw new ConflictException("Категория с именем: " + newCategoryDto.getName() + " уже существует");
        }
        Category category = CategoryMapper.toEntity(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.info("Категория создана с id: {}", savedCategory.getId());
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Попытка обновления категории с id: {}", categoryId);

        Category category = getCategoryEntity(categoryId);

        if (categoryRepository.existsByNameAndId(categoryDto.getName(), categoryId)) {
            return CategoryMapper.toDto(category);
        }

        if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw new ConflictException("new category should have new name");
        }

        Category updatedCategory = CategoryMapper.updateFromDto(categoryDto, category);
        Category savedCategory = categoryRepository.save(updatedCategory);

        log.info("Категория с id: {} успешно обновлена", savedCategory.getId());
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("Попытка удаление категории с id: {}", categoryId);

        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new NotFoundException("Категории с id: " + categoryId + " не существует");
        }

        if (categoryRepository.hasEvents(categoryId)) {
            throw new ConflictException("Невозможно удалить категорию с id: " + categoryId + " из за наличия событий");
        }

        categoryRepository.deleteById(categoryId);
        log.info("Категория с id: {} успешно удалена", categoryId);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        log.info("Получение категории с id: {}", categoryId);

        Category category = getCategoryEntity(categoryId);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Получение всех категорий из: {}, размером: {}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(List<Long> ids) {
        log.info("Получение категорий с ids: {}", ids);
        List<Category> categories = categoryRepository.findAllById(ids);

        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    private Category getCategoryEntity(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + categoryId + " не найдена"));
    }
}