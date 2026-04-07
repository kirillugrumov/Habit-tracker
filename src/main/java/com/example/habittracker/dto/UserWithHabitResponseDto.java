package com.example.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithHabitResponseDto {
    private Long userId;
    private String username;
    private String email;
    private Long habitId;
    private String habitName;
    private String habitDescription;
}