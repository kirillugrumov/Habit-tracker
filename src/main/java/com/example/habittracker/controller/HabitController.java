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

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<HabitResponseDto> createHabit(@RequestBody CreateHabitRequest request) {
        HabitResponseDto savedHabit = habitService.createHabit(request);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponseDto>> getAllHabits() {
        List<HabitResponseDto> habits = habitService.getAllHabits();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDto> getHabitById(@PathVariable Long id) {
        HabitResponseDto habit = habitService.getHabitById(id);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponseDto> updateHabit(
            @PathVariable Long id,
            @RequestBody UpdateHabitRequest request) {
        HabitResponseDto updatedHabit = habitService.updateHabit(id, request);
        return ResponseEntity.ok(updatedHabit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }



}