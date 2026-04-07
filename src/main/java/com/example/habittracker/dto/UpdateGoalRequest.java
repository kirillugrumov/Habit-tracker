package com.example.habittracker.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateGoalRequest extends GoalBasedRequestDto {
    public UpdateGoalRequest(String name, String condition, Long habitId) {
        super(name, condition, habitId);
    }
}