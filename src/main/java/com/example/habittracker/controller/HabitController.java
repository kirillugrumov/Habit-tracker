package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.UserWithHabitResponseDto;
import com.example.habittracker.dto.CreateUserWithHabitRequest;
import com.example.habittracker.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.habittracker.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Habits", description = "Operations for managing habits and habit demos")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    @Operation(summary = "Create habit", description = "Creates a new habit for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habit created",
                    content = @Content(schema = @Schema(implementation = HabitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Habit already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<HabitResponseDto> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        HabitResponseDto savedHabit = habitService.createHabit(request);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all habits", description = "Returns all habits.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habits returned successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<HabitResponseDto>> getAllHabits() {
        List<HabitResponseDto> habits = habitService.getAllHabits();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit by id", description = "Returns a habit by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habit returned successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<HabitResponseDto> getHabitById(@PathVariable Long id) {
        HabitResponseDto habit = habitService.getHabitById(id);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update habit", description = "Updates an existing habit by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habit updated successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Habit or category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Habit data conflict",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<HabitResponseDto> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitRequest request) {
        HabitResponseDto updatedHabit = habitService.updateHabit(id, request);
        return ResponseEntity.ok(updatedHabit);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete habit", description = "Deletes a habit by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Habit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/demo/problem")
    @Operation(summary = "Demo N+1 problem", description = "Returns habits using the intentionally" +
            " non-optimized query path.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habits returned successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<HabitResponseDto>> demoProblem() {
        List<HabitResponseDto> habits = habitService.getHabitsWithProblem();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/demo/solution")
    @Operation(summary = "Demo N+1 solution", description = "Returns habits using the optimized query path.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habits returned successfully"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<HabitResponseDto>> demoSolution() {
        List<HabitResponseDto> habits = habitService.getHabitsOptimized();
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Search habits via JPQL", description = "Searches habits by username and category using JPQL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search result returned successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Search habits via native SQL", description = "Searches habits by username and category " +
            "using a native query.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search result returned successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Create user and habit without transaction", description = "Demonstrates saving a user" +
            " and habit without wrapping the operation in a transaction.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User and habit created",
                    content = @Content(schema = @Schema(implementation = UserWithHabitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Create user and habit with transaction", description = "Demonstrates saving a user" +
            " and habit within a transaction.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User and habit created",
                    content = @Content(schema = @Schema(implementation = UserWithHabitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
