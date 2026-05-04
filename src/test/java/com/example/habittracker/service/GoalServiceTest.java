package com.example.habittracker.service;

import com.example.habittracker.dto.CreateGoalRequest;
import com.example.habittracker.dto.GoalResponseDto;
import com.example.habittracker.dto.UpdateGoalRequest;
import com.example.habittracker.mapper.GoalMapper;
import com.example.habittracker.model.Goal;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.GoalRepository;
import com.example.habittracker.repository.HabitRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalService goalService;

    @Test
    void createGoal_shouldSaveWhenValid() {
        CreateGoalRequest request = new CreateGoalRequest("Weekly cardio", "3 times", 1L);
        Habit habit = createHabit(1L);
        Goal goal = createGoal(null, "Weekly cardio", "3 times", habit);
        Goal savedGoal = createGoal(1L, "Weekly cardio", "3 times", habit);
        GoalResponseDto dto = new GoalResponseDto(1L, "Weekly cardio", "3 times", 1L, "Habit");

        when(goalRepository.existsByName("Weekly cardio")).thenReturn(false);
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(goalMapper.toEntity(request, habit)).thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(savedGoal);
        when(goalMapper.toResponseDto(savedGoal)).thenReturn(dto);

        GoalResponseDto result = goalService.createGoal(request);

        assertEquals(dto, result);
    }

    @Test
    void createGoal_shouldThrowWhenNameExists() {
        CreateGoalRequest request = new CreateGoalRequest("Weekly cardio", "3 times", 1L);
        when(goalRepository.existsByName("Weekly cardio")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> goalService.createGoal(request)
        );

        assertEquals("Goal with name 'Weekly cardio' already exists", exception.getMessage());
        verify(goalRepository, never()).save(any());
    }

    @Test
    void createGoal_shouldThrowWhenHabitMissing() {
        CreateGoalRequest request = new CreateGoalRequest("Weekly cardio", "3 times", 1L);
        when(goalRepository.existsByName("Weekly cardio")).thenReturn(false);
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> goalService.createGoal(request)
        );

        assertEquals("Habit not found with id: 1", exception.getMessage());
    }

    @Test
    void getAllGoals_shouldReturnMappedDtos() {
        Habit habit = createHabit(1L);
        Goal first = createGoal(1L, "G1", "C1", habit);
        Goal second = createGoal(2L, "G2", "C2", habit);
        GoalResponseDto firstDto = new GoalResponseDto(1L, "G1", "C1", 1L, "Habit");
        GoalResponseDto secondDto = new GoalResponseDto(2L, "G2", "C2", 1L, "Habit");

        when(goalRepository.findAll()).thenReturn(List.of(first, second));
        when(goalMapper.toResponseDto(first)).thenReturn(firstDto);
        when(goalMapper.toResponseDto(second)).thenReturn(secondDto);

        List<GoalResponseDto> result = goalService.getAllGoals();

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getGoalById_shouldReturnMappedDto() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "G1", "C1", habit);
        GoalResponseDto dto = new GoalResponseDto(1L, "G1", "C1", 1L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.getGoalById(1L);

        assertEquals(dto, result);
    }

    @Test
    void updateGoal_shouldUpdateAllFields() {
        Habit oldHabit = createHabit(1L);
        Habit newHabit = createHabit(2L);
        Goal goal = createGoal(1L, "Old", "Old condition", oldHabit);
        UpdateGoalRequest request = new UpdateGoalRequest("New", "New condition", 2L);
        GoalResponseDto dto = new GoalResponseDto(1L, "New", "New condition", 2L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.existsByName("New")).thenReturn(false);
        when(habitRepository.findById(2L)).thenReturn(Optional.of(newHabit));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.updateGoal(1L, request);

        assertEquals(dto, result);
        assertEquals("New", goal.getName());
        assertEquals("New condition", goal.getCondition());
        assertEquals(newHabit, goal.getHabit());
    }

    @Test
    void updateGoal_shouldThrowWhenNameTaken() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "Old", "Condition", habit);
        UpdateGoalRequest request = new UpdateGoalRequest("New", "New condition", null);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.existsByName("New")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> goalService.updateGoal(1L, request)
        );

        assertEquals("Name 'New' is already taken", exception.getMessage());
        verify(goalRepository, never()).save(any());
    }

    @Test
    void updateGoal_shouldThrowWhenNewHabitMissing() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "Old", "Condition", habit);
        UpdateGoalRequest request = new UpdateGoalRequest("Old", "New condition", 2L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(habitRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> goalService.updateGoal(1L, request)
        );

        assertEquals("Habit not found with id: 2", exception.getMessage());
    }

    @Test
    void deleteGoal_shouldDeleteByIdWhenExists() {
        when(goalRepository.existsById(1L)).thenReturn(true);

        goalService.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    void deleteGoal_shouldThrowWhenMissing() {
        when(goalRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> goalService.deleteGoal(1L)
        );

        assertEquals("Goal not found with id: 1", exception.getMessage());
    }

    private Habit createHabit(Long id) {
        Habit habit = new Habit();
        habit.setId(id);
        habit.setName("Habit");
        return habit;
    }

    private Goal createGoal(Long id, String name, String condition, Habit habit) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setName(name);
        goal.setCondition(condition);
        goal.setHabit(habit);
        return goal;
    }

    // ========== ПОКРЫТИЕ ВЕТОК В updateGoal ==========

    @Test
    void updateGoal_shouldNotUpdateName_whenNameIsNull() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "OldGoal", "OldCondition", habit);
        UpdateGoalRequest request = new UpdateGoalRequest(null, "NewCondition", null);
        GoalResponseDto dto = new GoalResponseDto(1L, "OldGoal", "NewCondition", 1L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.updateGoal(1L, request);

        assertEquals("OldGoal", goal.getName());
        assertEquals("NewCondition", goal.getCondition());
        verify(goalRepository, never()).existsByName(any());
    }

    @Test
    void updateGoal_shouldNotUpdateName_whenNameIsSame() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "SameGoal", "OldCondition", habit);
        UpdateGoalRequest request = new UpdateGoalRequest("SameGoal", "NewCondition", null);
        GoalResponseDto dto = new GoalResponseDto(1L, "SameGoal", "NewCondition", 1L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.updateGoal(1L, request);

        assertEquals("SameGoal", goal.getName());
        verify(goalRepository, never()).existsByName(any());
    }

    @Test
    void updateGoal_shouldNotUpdateCondition_whenConditionIsNull() {
        Habit habit = createHabit(1L);
        Goal goal = createGoal(1L, "Goal", "OldCondition", habit);
        UpdateGoalRequest request = new UpdateGoalRequest("NewName", null, null);
        GoalResponseDto dto = new GoalResponseDto(1L, "NewName", "OldCondition", 1L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.updateGoal(1L, request);

        assertEquals("OldCondition", goal.getCondition());
        assertEquals("NewName", goal.getName());
    }

    @Test
    void updateGoal_shouldNotUpdateHabit_whenHabitIdIsNull() {
        Habit oldHabit = createHabit(1L);
        Goal goal = createGoal(1L, "Goal", "Condition", oldHabit);
        UpdateGoalRequest request = new UpdateGoalRequest("NewName", "NewCondition", null);
        GoalResponseDto dto = new GoalResponseDto(1L, "NewName", "NewCondition", 1L, "Habit");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toResponseDto(goal)).thenReturn(dto);

        GoalResponseDto result = goalService.updateGoal(1L, request);

        assertEquals(oldHabit, goal.getHabit());
        verify(habitRepository, never()).findById(any());
    }
}
