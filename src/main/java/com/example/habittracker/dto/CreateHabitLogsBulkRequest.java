package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request body for bulk creating habit logs")
public class CreateHabitLogsBulkRequest {

    @ArraySchema(schema = @Schema(implementation = CreateHabitLogRequest.class))
    @NotEmpty(message = "At least one habit log request is required")
    @Valid
    private List<CreateHabitLogRequest> logs = new ArrayList<>();

    public CreateHabitLogsBulkRequest() {
    }

    public CreateHabitLogsBulkRequest(List<CreateHabitLogRequest> logs) {
        this.logs = logs != null ? logs : new ArrayList<>();
    }

    public List<CreateHabitLogRequest> getLogs() {
        return logs;
    }

    public void setLogs(List<CreateHabitLogRequest> logs) {
        this.logs = logs != null ? logs : new ArrayList<>();
    }
}
