package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Habit response")
public class HabitResponseDto {
    @Schema(description = "Habit id", example = "10")
    private Long id;
    @Schema(description = "Habit name", example = "Morning Workout")
    private String name;
    @Schema(description = "Habit description", example = "30 minutes of exercise before breakfast")
    private String description;
    @Schema(description = "Owner user id", example = "1")
    private Long userId;
    @Schema(description = "Owner username", example = "john_doe")
    private String username;
    @Schema(description = "Habit categories")
    private List<CategoryInfo> categories;

    @Data
    @AllArgsConstructor
    @Schema(description = "Short category information")
    public static class CategoryInfo {
        @Schema(description = "Category id", example = "2")
        private Long id;
        @Schema(description = "Category name", example = "Fitness")
        private String name;
    }
}
