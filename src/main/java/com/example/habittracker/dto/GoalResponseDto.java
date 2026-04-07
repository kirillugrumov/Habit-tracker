package com.example.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoalResponseDto {
    private Long id;
    private String name;
    private String condition;
    private Long habitId;
    private String habitName;
}