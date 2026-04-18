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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;
    private final GoalMapper goalMapper;

    public GoalService(GoalRepository goalRepository,
                       HabitRepository habitRepository,
                       GoalMapper goalMapper) {
        this.goalRepository = goalRepository;
        this.habitRepository = habitRepository;
        this.goalMapper = goalMapper;
    }

    @Transactional
    public GoalResponseDto createGoal(CreateGoalRequest request) {
        if (goalRepository.existsByName(request.getName())) {
            throw new EntityExistsException("Goal with name '" + request.getName() + "' already exists");
        }

        Habit habit = habitRepository.findById(request.getHabitId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Habit not found with id: " + request.getHabitId()
                ));

        Goal goal = goalMapper.toEntity(request, habit);
        Goal savedGoal = goalRepository.save(goal);
        return goalMapper.toResponseDto(savedGoal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponseDto> getAllGoals() {
        List<Goal> goals = goalRepository.findAll();

        return goals.stream()
                .map(goalMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponseDto getGoalById(Long id) {
        Goal goal = getGoalByIdEntity(id);
        return goalMapper.toResponseDto(goal);
    }

    @Transactional
    public GoalResponseDto updateGoal(Long id, UpdateGoalRequest request) {
        Goal goal = getGoalByIdEntity(id);

        if (request.getName() != null && !request.getName().equals(goal.getName())) {
            if (goalRepository.existsByName(request.getName())) {
                throw new EntityExistsException("Name '" + request.getName() + "' is already taken");
            }
            goal.setName(request.getName());
        }

        if (request.getCondition() != null) {
            goal.setCondition(request.getCondition());
        }

        if (request.getHabitId() != null) {
            Habit habit = habitRepository.findById(request.getHabitId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Habit not found with id: " + request.getHabitId()
                    ));
            goal.setHabit(habit);
        }

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponseDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new EntityNotFoundException("Goal not found with id: " + id);
        }
        goalRepository.deleteById(id);
    }

    private Goal getGoalByIdEntity(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + id));
    }
}
