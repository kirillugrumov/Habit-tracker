package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Goal response")
public class GoalResponseDto {
    @Schema(description = "Goal id", example = "1")
    private Long id;
    @Schema(description = "Goal name", example = "Weekly cardio")
    private String name;
    @Schema(description = "Goal condition", example = "Run at least 3 times a week")
    private String condition;
    @Schema(description = "Related habit id", example = "10")
    private Long habitId;
    @Schema(description = "Related habit name", example = "Morning Run")
    private String habitName;
}
