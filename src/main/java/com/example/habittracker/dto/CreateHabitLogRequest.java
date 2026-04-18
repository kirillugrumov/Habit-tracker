package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for creating a habit log")
public class CreateHabitLogRequest {
    @Schema(description = "Habit id to create a log for", example = "10")
    @NotNull(message = "Habit ID is required")
    @Positive(message = "Habit ID must be positive")
    private Long habitId;

    public CreateHabitLogRequest() {
    }

    public CreateHabitLogRequest(Long habitId) {
        this.habitId = habitId;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }
}
