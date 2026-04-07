package com.example.habittracker.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateCategoryRequest extends NamedEntityRequestDto {
    public UpdateCategoryRequest(String name, String description) {
        super(name, description);
    }
}