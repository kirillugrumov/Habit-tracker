package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(description = "Request body for creating a goal")
public class CreateGoalRequest extends GoalBasedRequestDto {
    public CreateGoalRequest(String name, String condition, Long habitId) {
        super(name, condition, habitId);
    }
}
