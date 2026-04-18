package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.service.HabitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<HabitLogResponseDto> createHabitLog(@Valid @RequestBody CreateHabitLogRequest request) {
        HabitLogResponseDto savedLog = habitLogService.createHabitLog(request);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all habit logs", description = "Returns all habit log entries.")
    public ResponseEntity<List<HabitLogResponseDto>> getAllHabitLogs() {
        List<HabitLogResponseDto> logs = habitLogService.getAllHabitLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit log by id", description = "Returns a habit log by its identifier.")
    public ResponseEntity<HabitLogResponseDto> getHabitLogById(@PathVariable Long id) {
        HabitLogResponseDto log = habitLogService.getHabitLogById(id);
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete habit log", description = "Deletes a habit log by id.")
    public ResponseEntity<Void> deleteHabitLog(@PathVariable Long id) {
        habitLogService.deleteHabitLog(id);
        return ResponseEntity.noContent().build();
    }
}
