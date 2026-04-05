package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.service.HabitLogService;
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
public class HabitLogController {

    private final HabitLogService habitLogService;

    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    @PostMapping
    public ResponseEntity<HabitLogResponseDto> createHabitLog(@RequestBody CreateHabitLogRequest request) {
        HabitLogResponseDto savedLog = habitLogService.createHabitLog(request);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HabitLogResponseDto>> getAllHabitLogs() {
        List<HabitLogResponseDto> logs = habitLogService.getAllHabitLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitLogResponseDto> getHabitLogById(@PathVariable Long id) {
        HabitLogResponseDto log = habitLogService.getHabitLogById(id);
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitLog(@PathVariable Long id) {
        habitLogService.deleteHabitLog(id);
        return ResponseEntity.noContent().build();
    }

}