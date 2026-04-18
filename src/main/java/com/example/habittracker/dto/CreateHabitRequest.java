package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request body for creating a habit")
public class CreateHabitRequest {
    @Schema(description = "Habit name", example = "Morning Workout")
    @NotBlank(message = "Habit name is required")
    private String name;

    @Schema(description = "Optional habit description", example = "30 minutes of exercise before breakfast")
    private String description; // optional

    @Schema(description = "Owner user id", example = "1")
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    @ArraySchema(schema = @Schema(description = "Category id", example = "2"))
    private List<Long> categoryIds = new ArrayList<>();

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String name, String description, Long userId, List<Long> categoryIds) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
    }
}
