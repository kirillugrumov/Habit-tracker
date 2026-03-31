package com.example.habittracker.mapper;

import com.example.habittracker.dto.CategoryResponseDto;
import com.example.habittracker.dto.CreateCategoryRequest;
import com.example.habittracker.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        return new Category(
                request.getName(),
                request.getDescription()
        );
    }
}