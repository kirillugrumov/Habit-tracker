package com.example.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class HabitLogResponseDto {
    private Long id;
    private Long habitId;
    private LocalDate logDate;
}