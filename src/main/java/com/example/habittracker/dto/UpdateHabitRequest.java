package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "Request body for updating a habit")
public class UpdateHabitRequest {
    @Schema(description = "Updated habit name", example = "Evening Workout")
    @NotBlank(message = "Habit name is required")
    private String name;

    @Schema(description = "Updated habit description", example = "45 minutes of exercise after work")
    private String description; // optional

    @ArraySchema(schema = @Schema(description = "Category id", example = "3"))
    private List<Long> categoryIds;

    public UpdateHabitRequest() {
    }

    public UpdateHabitRequest(String name, String description, List<Long> categoryIds) {
        this.name = name;
        this.description = description;
        this.categoryIds = categoryIds;
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

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
