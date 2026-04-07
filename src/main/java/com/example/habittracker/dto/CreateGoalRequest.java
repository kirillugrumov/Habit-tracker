package com.example.habittracker.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CreateGoalRequest extends GoalBasedRequestDto {
    public CreateGoalRequest(String name, String condition, Long habitId) {
        super(name, condition, habitId);
    }
}