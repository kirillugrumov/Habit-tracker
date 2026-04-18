package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(description = "Request body for updating a category")
public class UpdateCategoryRequest extends NamedEntityRequestDto {
    public UpdateCategoryRequest(String name, String description) {
        super(name, description);
    }

    @NotBlank(message = "Category name is required")
    @Override
    public String getName() {
        return super.getName();
    }
}
