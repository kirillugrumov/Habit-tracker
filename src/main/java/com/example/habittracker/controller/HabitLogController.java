package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.dto.UpdateHabitLogRequest;
import com.example.habittracker.mapper.HabitLogMapper;
import com.example.habittracker.model.HabitLog;
import com.example.habittracker.service.HabitLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habit-logs")
public class HabitLogController {

    private final HabitLogService habitLogService;
    private final HabitLogMapper habitLogMapper;

    public HabitLogController(HabitLogService habitLogService, HabitLogMapper habitLogMapper) {
        this.habitLogService = habitLogService;
        this.habitLogMapper = habitLogMapper;
    }

    @GetMapping
    public ResponseEntity<List<HabitLogResponseDto>> getHabitLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long habitId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "false") boolean withDetails) {

        List<HabitLog> habitLogs;

        if (userId != null && startDate != null && endDate != null) {
            habitLogs = habitLogService.getHabitLogsByDateRange(userId, startDate, endDate);
        } else if (userId != null) {
            if (withDetails) {
                habitLogs = habitLogService.getHabitLogsByUserIdWithDetails(userId);
            } else {
                habitLogs = habitLogService.getHabitLogsByUserIdWithDetails(userId);
            }
        } else if (habitId != null) {
            habitLogs = habitLogService.getHabitLogsByHabitIdWithDetails(habitId);
        } else {
            habitLogs = List.of();
        }

        List<HabitLogResponseDto> response = habitLogs.stream()
                .map(habitLogMapper::toDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitLogResponseDto> getHabitLogById(@PathVariable Long id) {
        HabitLog habitLog = habitLogService.getHabitLogById(id);
        return ResponseEntity.ok(habitLogMapper.toDto(habitLog));
    }

    @PostMapping
    public ResponseEntity<HabitLogResponseDto> createHabitLog(
            @RequestBody CreateHabitLogRequest request) {

        HabitLog habitLog = habitLogService.createHabitLog(
                request.getHabitId(),
                request.getUserId(),
                request.getCompletionDate(),
                request.isCompleted()
        );

        return new ResponseEntity<>(
                habitLogMapper.toDto(habitLog),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitLogResponseDto> updateHabitLog(
            @PathVariable Long id,
            @RequestBody UpdateHabitLogRequest request) {

        HabitLog updatedLog = habitLogService.updateHabitLog(
                id,
                request.getCompleted(),
                request.getNotes()
        );

        return ResponseEntity.ok(habitLogMapper.toDto(updatedLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitLog(@PathVariable Long id) {
        habitLogService.deleteHabitLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/demo/n-plus-one/{userId}")
    public ResponseEntity<String> demonstrateNPlusOne(@PathVariable Long userId) {
        habitLogService.demonstrateNPlusOneProblem(userId);
        return ResponseEntity.ok("Проверьте логи - проблема N+1 продемонстрирована");
    }

    @GetMapping("/demo/solution/{userId}")
    public ResponseEntity<String> demonstrateSolution(@PathVariable Long userId) {
        habitLogService.demonstrateSolutionWithEntityGraph(userId);
        return ResponseEntity.ok("Проверьте логи - проблема N+1 решена через EntityGraph");
    }

    @PostMapping("/demo/without-transaction")
    public ResponseEntity<String> demoWithoutTransaction() {
        try {
            habitLogService.saveWithoutTransaction();
            return ResponseEntity.ok("Логи сохранены (частично)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка: " + e.getMessage() + " (первые логи сохранены)");
        }
    }

    @PostMapping("/demo/with-transaction")
    public ResponseEntity<String> demoWithTransaction() {
        try {
            habitLogService.saveWithTransaction();
            return ResponseEntity.ok("Логи сохранены (полностью)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка: " + e.getMessage() + " (все логи откачены)");
        }
    }
}