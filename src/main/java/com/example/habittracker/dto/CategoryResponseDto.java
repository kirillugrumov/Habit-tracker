package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Category response")
public class CategoryResponseDto {
    @Schema(description = "Category id", example = "1")
    private Long id;
    @Schema(description = "Category name", example = "Health")
    private String name;
    @Schema(description = "Category description", example = "Activities related to wellness")
    private String description;
}
