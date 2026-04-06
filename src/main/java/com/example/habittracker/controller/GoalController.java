package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateGoalRequest;
import com.example.habittracker.dto.UpdateGoalRequest;
import com.example.habittracker.dto.GoalResponseDto;
import com.example.habittracker.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody CreateGoalRequest request) {
        GoalResponseDto savedGoal = goalService.createGoal(request);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDto>> getAllGoals() {
        List<GoalResponseDto> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponseDto> getGoalById(@PathVariable Long id) {
        GoalResponseDto goal = goalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable Long id,
            @RequestBody UpdateGoalRequest request) {
        GoalResponseDto updatedGoal = goalService.updateGoal(id, request);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}