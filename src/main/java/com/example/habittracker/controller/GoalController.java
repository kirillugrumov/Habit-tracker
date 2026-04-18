package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateGoalRequest;
import com.example.habittracker.dto.UpdateGoalRequest;
import com.example.habittracker.dto.GoalResponseDto;
import com.example.habittracker.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Goals", description = "Operations for managing goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @Operation(summary = "Create goal", description = "Creates a new goal linked to a habit.")
    public ResponseEntity<GoalResponseDto> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalResponseDto savedGoal = goalService.createGoal(request);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all goals", description = "Returns all saved goals.")
    public ResponseEntity<List<GoalResponseDto>> getAllGoals() {
        List<GoalResponseDto> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by id", description = "Returns a goal by its identifier.")
    public ResponseEntity<GoalResponseDto> getGoalById(@PathVariable Long id) {
        GoalResponseDto goal = goalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Updates an existing goal by id.")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request) {
        GoalResponseDto updatedGoal = goalService.updateGoal(id, request);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", description = "Deletes a goal by id.")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
