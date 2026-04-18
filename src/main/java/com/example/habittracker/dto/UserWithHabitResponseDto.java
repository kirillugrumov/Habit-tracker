package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Combined user and habit response")
public class UserWithHabitResponseDto {
    @Schema(description = "User id", example = "1")
    private Long userId;
    @Schema(description = "Username", example = "anna")
    private String username;
    @Schema(description = "User email", example = "anna@example.com")
    private String email;
    @Schema(description = "Habit id", example = "10")
    private Long habitId;
    @Schema(description = "Habit name", example = "Read 20 pages")
    private String habitName;
    @Schema(description = "Habit description", example = "Read every evening before sleep")
    private String habitDescription;
}
