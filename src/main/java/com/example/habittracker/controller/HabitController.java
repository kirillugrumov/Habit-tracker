package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.mapper.HabitMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.service.HabitService;
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

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<HabitResponseDto>> getAllHabits() {
        List<Habit> habits = habitService.getAllHabits();
        List<HabitResponseDto> response = habits.stream()
                .map(habitMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HabitResponseDto>> searchHabitsByName(
            @RequestParam String name) {
        List<Habit> habits = habitService.getHabitsByName(name);
        List<HabitResponseDto> response = habits.stream()
                .map(habitMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDto> getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id)
                .map(habitMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HabitResponseDto> createHabit(
            @RequestBody CreateHabitRequest request) {

        Category category = null;
        if (request.getCategoryId() != null) {
            category = habitService.getCategoryById(request.getCategoryId());
        }

        Habit habit = habitMapper.toEntity(request, category);
        Habit savedHabit = habitService.createHabit(habit);

        return new ResponseEntity<>(
                habitMapper.toDto(savedHabit),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponseDto> updateHabit(
            @PathVariable Long id,
            @RequestBody UpdateHabitRequest request) {

        Habit existingHabit = habitService.getHabitById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = habitService.getCategoryById(request.getCategoryId());
        }

        habitMapper.updateEntity(existingHabit, request, category);
        Habit updatedHabit = habitService.updateHabit(id, existingHabit);

        return ResponseEntity.ok(habitMapper.toDto(updatedHabit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }
}