package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {

    public HabitResponseDto toDto(Habit habit) {
        if (habit == null) {
            return null;
        }

        return new HabitResponseDto(
                habit.getId(),
                habit.getName(),
                habit.getCompletionCount()
        );
    }

    public Habit toEntity(CreateHabitRequest request, Category category) {
        if (request == null) {
            return null;
        }

        Habit habit = new Habit();
        habit.setName(request.getName());

        habit.setCompletionCount(request.getCompletionCount() != null ? request.getCompletionCount() : 0);

        habit.setCategory(category);

        return habit;
    }

    public void updateEntity(Habit habit, UpdateHabitRequest request, Category category) {
        if (request == null || habit == null) {
            return;
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            habit.setName(request.getName());
        }

        if (request.getCompletionCount() != null) {
            habit.setCompletionCount(request.getCompletionCount());
        }

        if (category != null) {
            habit.setCategory(category);
        } else if (request.getCategoryId() == null) {
            habit.setCategory(null);
        }
    }
}