package com.example.habittracker.service;

import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.model.Category;
import com.example.habittracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByName(String name) {
        if (name == null || name.isBlank()) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Категория с именем '" + category.getName() + "' уже существует");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, UpdateCategoryRequest request) {
        Category existingCategory = getCategoryById(id);

        if (request.getName() != null && !request.getName().equals(existingCategory.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new RuntimeException("Категория с именем '" + request.getName() + "' уже существует");
            }
            existingCategory.setName(request.getName());
        }

        if (request.getDescription() != null) {
            existingCategory.setDescription(request.getDescription());
        }

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        if (!category.getHabits().isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию, которая содержит привычки");
        }

        categoryRepository.deleteById(id);
    }
}