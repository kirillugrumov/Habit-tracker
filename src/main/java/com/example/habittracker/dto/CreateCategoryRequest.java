package com.example.habittracker.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CreateCategoryRequest extends NamedEntityRequestDto {
    public CreateCategoryRequest(String name, String description) {
        super(name, description);
    }
}