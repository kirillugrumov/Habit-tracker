package com.example.habittracker.service;

import com.example.habittracker.cache.HabitSearchCache;
import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.mapper.CategoryMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final HabitSearchCache habitSearchCache;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryMapper categoryMapper,
                           HabitSearchCache habitSearchCache) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.habitSearchCache = habitSearchCache;
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
            throw new EntityExistsException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        habitSearchCache.invalidateAll();
        return categoryMapper.toResponseDto(savedCategory);
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryByIdEntity(id);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new EntityExistsException("Name '" + request.getName() + "' is already taken");
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        Category updatedCategory = categoryRepository.save(category);
        habitSearchCache.invalidateAll();
        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }

        categoryRepository.deleteCategoryLinks(id);
        categoryRepository.deleteById(id);
        habitSearchCache.invalidateAll();
    }

    private Category getCategoryByIdEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}
