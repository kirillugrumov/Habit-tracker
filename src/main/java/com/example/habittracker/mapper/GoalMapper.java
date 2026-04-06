package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateGoalRequest;
import com.example.habittracker.dto.UpdateGoalRequest;
import com.example.habittracker.dto.GoalResponseDto;
import com.example.habittracker.model.Goal;
import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {

    public GoalResponseDto toResponseDto(Goal goal) {
        if (goal == null) {
            return null;
        }

        return new GoalResponseDto(
                goal.getId(),
                goal.getName(),
                goal.getCondition(),
                goal.getHabit() != null ? goal.getHabit().getId() : null,
                goal.getHabit() != null ? goal.getHabit().getName() : null
        );
    }

    public Goal toEntity(CreateGoalRequest request, Habit habit) {
        if (request == null) {
            return null;
        }

        return new Goal(
                request.getName(),
                request.getCondition(),
                habit
        );
    }

    public void updateEntity(Goal goal, UpdateGoalRequest request, Habit habit) {
        if (request == null || goal == null) {
            return;
        }

        if (request.getName() != null) {
            goal.setName(request.getName());
        }

        if (request.getCondition() != null) {
            goal.setCondition(request.getCondition());
        }

        if (habit != null) {
            goal.setHabit(habit);
        }
    }
}