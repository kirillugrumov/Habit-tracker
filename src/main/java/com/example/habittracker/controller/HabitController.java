package com.example.habittracker.controller;

import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.mapper.HabitMapper;
import com.example.habittracker.model.Habit;
import com.example.habittracker.service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;
    private final HabitMapper habitMapper;

    public HabitController(HabitService habitService,
                           HabitMapper habitMapper) {
        this.habitService = habitService;
        this.habitMapper = habitMapper;
    }

    // GET with @PathVariable
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDto> getHabitById(
            @PathVariable Long id) {

        return habitService.getHabitById(id)
                .map(habitMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET with @RequestParam
    @GetMapping
    public ResponseEntity<List<HabitResponseDto>> getHabits(
            @RequestParam(required = false) String name) {

        List<Habit> habits = (name == null)
                ? habitService.getAllHabits()
                : habitService.getHabitsByName(name);

        List<HabitResponseDto> response = habits.stream()
                .map(habitMapper::toDto)
                .toList();

        return ResponseEntity.ok(response);
    }
}