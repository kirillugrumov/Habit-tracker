package com.example.habittracker.controller;

import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.mapper.CategoryMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(
            @RequestParam(required = false) String name) {
        List<Category> categories = categoryService.getCategoriesByName(name);
        List<CategoryResponseDto> response = categories.stream()
                .map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody CreateCategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(categoryMapper.toDto(savedCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request) {
        Category updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(categoryMapper.toDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}