package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response body for bulk habit log creation")
public class BulkHabitLogResponseDto {

    @Schema(description = "Created habit logs")
    private List<HabitLogResponseDto> logs;

    public BulkHabitLogResponseDto() {
    }

    public BulkHabitLogResponseDto(List<HabitLogResponseDto> logs) {
        this.logs = logs;
    }

    public List<HabitLogResponseDto> getLogs() {
        return logs;
    }

    public void setLogs(List<HabitLogResponseDto> logs) {
        this.logs = logs;
    }
}
