package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Schema(description = "Habit log response")
public class HabitLogResponseDto {
    @Schema(description = "Habit log id", example = "5")
    private Long id;
    @Schema(description = "Habit id", example = "10")
    private Long habitId;
    @Schema(description = "Date when the habit was logged", example = "2026-04-18")
    private LocalDate logDate;
}
