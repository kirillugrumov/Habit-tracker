package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {

    public Habit toEntity(CreateHabitRequest request, Category category) {
        if (request == null) {
            return null;
        }

        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setCategory(category);
        return habit;
    }

    public void updateEntity(Habit habit, UpdateHabitRequest request, Category category) {
        if (request == null || habit == null) {
            return;
        }

        if (request.getName() != null) {
            habit.setName(request.getName());
        }

        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }

        if (category != null) {
            habit.setCategory(category);
        } else if (request.isRemoveCategory()) {
            habit.setCategory(null);
        }
    }

    public HabitResponseDto toResponseDto(Habit habit) {
        if (habit == null) {
            return null;
        }

        return new HabitResponseDto(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getUser() != null ? habit.getUser().getId() : null,
                habit.getUser() != null ? habit.getUser().getUsername() : null,
                habit.getCategory() != null ? habit.getCategory().getId() : null,
                habit.getCategory() != null ? habit.getCategory().getName() : null
        );
    }
}