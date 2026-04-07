package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HabitMapper {

    public Habit toEntity(CreateHabitRequest request, List<Category> categories) {
        if (request == null) {
            return null;
        }

        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setCategories(categories != null ? categories : new ArrayList<>());
        return habit;
    }

    public void updateEntity(Habit habit, UpdateHabitRequest request, List<Category> categories) {
        if (request == null || habit == null) {
            return;
        }

        if (request.getName() != null) {
            habit.setName(request.getName());
        }

        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }

        if (categories != null) {
            habit.setCategories(categories);
        }
    }

    public HabitResponseDto toResponseDto(Habit habit) {
        if (habit == null) {
            return null;
        }

        List<HabitResponseDto.CategoryInfo> categoryInfos = habit.getCategories().stream()
                .map(category -> new HabitResponseDto.CategoryInfo(
                        category.getId(),
                        category.getName()
                ))
                .toList();

        return new HabitResponseDto(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getUser() != null ? habit.getUser().getId() : null,
                habit.getUser() != null ? habit.getUser().getUsername() : null,
                categoryInfos
        );
    }
}