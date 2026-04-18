package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(description = "Request body for updating a goal")
public class UpdateGoalRequest extends GoalBasedRequestDto {
    public UpdateGoalRequest(String name, String condition, Long habitId) {
        super(name, condition, habitId);
    }

    @NotBlank(message = "Goal name is required")
    @Override
    public String getName() {
        return super.getName();
    }

    @NotBlank(message = "Condition is required")
    @Override
    public String getCondition() {
        return super.getCondition();
    }

    @NotNull(message = "Habit ID is required")
    @Positive(message = "Habit ID must be positive")
    @Override
    public Long getHabitId() {
        return super.getHabitId();
    }
}
