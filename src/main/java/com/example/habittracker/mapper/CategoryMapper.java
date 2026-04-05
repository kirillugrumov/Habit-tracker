package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.dto.UpdateCategoryRequest;
import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return category;
    }

    public void updateEntity(Category category, UpdateCategoryRequest request) {
        if (request == null || category == null) {
            return;
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }

    public CategoryResponseDto toResponseDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}