package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.UserWithHabitResponseDto;
import com.example.habittracker.dto.CreateUserWithHabitRequest;
import com.example.habittracker.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habits", description = "Operations for managing habits and habit demos")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    @Operation(summary = "Create habit", description = "Creates a new habit for a user.")
    public ResponseEntity<HabitResponseDto> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        HabitResponseDto savedHabit = habitService.createHabit(request);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all habits", description = "Returns all habits.")
    public ResponseEntity<List<HabitResponseDto>> getAllHabits() {
        List<HabitResponseDto> habits = habitService.getAllHabits();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit by id", description = "Returns a habit by its identifier.")
    public ResponseEntity<HabitResponseDto> getHabitById(@PathVariable Long id) {
        HabitResponseDto habit = habitService.getHabitById(id);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update habit", description = "Updates an existing habit by id.")
    public ResponseEntity<HabitResponseDto> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitRequest request) {
        HabitResponseDto updatedHabit = habitService.updateHabit(id, request);
        return ResponseEntity.ok(updatedHabit);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete habit", description = "Deletes a habit by id.")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/demo/problem")
    @Operation(summary = "Demo N+1 problem", description = "Returns habits using the intentionally non-optimized query path.")
    public ResponseEntity<List<HabitResponseDto>> demoProblem() {
        List<HabitResponseDto> habits = habitService.getHabitsWithProblem();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/demo/solution")
    @Operation(summary = "Demo N+1 solution", description = "Returns habits using the optimized query path.")
    public ResponseEntity<List<HabitResponseDto>> demoSolution() {
        List<HabitResponseDto> habits = habitService.getHabitsOptimized();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Search habits via JPQL", description = "Searches habits by username and category using JPQL.")
    public ResponseEntity<Page<HabitResponseDto>> searchHabitsJpql(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String categoryName,
            Pageable pageable) {
        Page<HabitResponseDto> habits = habitService.searchHabitsByUserAndCategoryJpql(
                username,
                categoryName,
                pageable
        );
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/search/native")
    @Operation(summary = "Search habits via native SQL", description = "Searches habits by username and category using a native query.")
    public ResponseEntity<Page<HabitResponseDto>> searchHabitsNative(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String categoryName,
            Pageable pageable) {
        Page<HabitResponseDto> habits = habitService.searchHabitsByUserAndCategoryNative(
                username,
                categoryName,
                pageable
        );
        return ResponseEntity.ok(habits);
    }

    @PostMapping("/demo/save-without-tx")
    @Operation(summary = "Create user and habit without transaction", description = "Demonstrates saving a user and habit without wrapping the operation in a transaction.")
    public ResponseEntity<UserWithHabitResponseDto> saveUserAndHabitWithoutTransaction(
            @Valid @RequestBody CreateUserWithHabitRequest request) {
        UserWithHabitResponseDto response = habitService.saveUserAndHabitWithoutTransaction(
                request.getUsername(),
                request.getEmail(),
                request.getHabitName(),
                request.getHabitDescription()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/demo/save-with-tx")
    @Operation(summary = "Create user and habit with transaction", description = "Demonstrates saving a user and habit within a transaction.")
    public ResponseEntity<UserWithHabitResponseDto> saveUserAndHabitWithTransaction(
            @Valid @RequestBody CreateUserWithHabitRequest request) {
        UserWithHabitResponseDto response = habitService.saveUserAndHabitWithTransaction(
                request.getUsername(),
                request.getEmail(),
                request.getHabitName(),
                request.getHabitDescription()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
