package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.habittracker.service.HabitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habit-logs")
@Tag(name = "Habit Logs", description = "Operations for managing habit logs")
public class HabitLogController {

    private final HabitLogService habitLogService;

    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    @PostMapping
    @Operation(summary = "Create habit log", description = "Creates a log entry for a habit.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Habit log created",
                    content = @Content(schema = @Schema(implementation = HabitLogResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<HabitLogResponseDto> createHabitLog(@Valid @RequestBody CreateHabitLogRequest request) {
        HabitLogResponseDto savedLog = habitLogService.createHabitLog(request);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all habit logs", description = "Returns all habit log entries.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Habit logs returned successfully"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<HabitLogResponseDto>> getAllHabitLogs() {
        List<HabitLogResponseDto> logs = habitLogService.getAllHabitLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit log by id", description = "Returns a habit log by its identifier.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Habit log returned successfully",
                    content = @Content(schema = @Schema(implementation = HabitLogResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Habit log not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<HabitLogResponseDto> getHabitLogById(@PathVariable Long id) {
        HabitLogResponseDto log = habitLogService.getHabitLogById(id);
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete habit log", description = "Deletes a habit log by id.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Habit log deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Habit log not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteHabitLog(@PathVariable Long id) {
        habitLogService.deleteHabitLog(id);
        return ResponseEntity.noContent().build();
    }
}
