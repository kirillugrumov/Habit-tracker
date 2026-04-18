package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(description = "Request body for updating a goal")
public class UpdateGoalRequest extends GoalBasedRequestDto {
    public UpdateGoalRequest(String name, String condition, Long habitId) {
        super(name, condition, habitId);
    }
}
