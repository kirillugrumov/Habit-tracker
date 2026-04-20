package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateGoalRequest;
import com.example.habittracker.dto.UpdateGoalRequest;
import com.example.habittracker.dto.GoalResponseDto;
import com.example.habittracker.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.habittracker.service.GoalService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Goal created",
                    content = @Content(schema = @Schema(implementation = GoalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Related habit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Goal already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GoalResponseDto> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalResponseDto savedGoal = goalService.createGoal(request);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all goals", description = "Returns all saved goals.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goals returned successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<GoalResponseDto>> getAllGoals() {
        List<GoalResponseDto> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by id", description = "Returns a goal by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal returned successfully",
                    content = @Content(schema = @Schema(implementation = GoalResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Goal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GoalResponseDto> getGoalById(@PathVariable Long id) {
        GoalResponseDto goal = goalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Updates an existing goal by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal updated successfully",
                    content = @Content(schema = @Schema(implementation = GoalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Goal or related habit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Goal data conflict",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request) {
        GoalResponseDto updatedGoal = goalService.updateGoal(id, request);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", description = "Deletes a goal by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Goal not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
