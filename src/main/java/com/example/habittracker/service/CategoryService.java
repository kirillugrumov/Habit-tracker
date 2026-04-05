package com.example.habittracker.service;

import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.mapper.CategoryMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(categoryMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = getCategoryByIdEntity(id);
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Категория с именем '" + request.getName() + "' уже существует");
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(savedCategory);
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryByIdEntity(id);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new RuntimeException("Имя '" + request.getName() + "' уже занято");
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Категория не найдена с id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category getCategoryByIdEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + id));
    }
}