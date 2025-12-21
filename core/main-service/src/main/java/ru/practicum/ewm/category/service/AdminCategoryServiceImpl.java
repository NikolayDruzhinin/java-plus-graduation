package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ValidationException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.mapToCategory(newCategoryDto);
        checkDuplicateCategoryByName(category);
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        Category category = CategoryMapper.mapToCategory(categoryDto);
        category.setId(id);
        checkDuplicateCategoryByName(category);

        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        checkCategoryIsNotUse(id);
        categoryRepository.deleteById(id);
    }

    private void checkDuplicateCategoryByName(Category category) {
        Category existCategory = categoryRepository.findByName(category.getName());

        if (existCategory != null && existCategory.getId() != category.getId()) {
            throw new ValidationException("Категория с name = " + category.getName() + " уже существует");
        }
    }

    private void checkCategoryIsNotUse(Long id) {
        if (eventRepository.existsByCategoryId(id)) {
            throw new ValidationException("Нельзя удалить категорию, с которой связаны события");
        }
    }
}
