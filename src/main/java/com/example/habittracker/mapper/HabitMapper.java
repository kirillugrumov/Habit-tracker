package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HabitMapper {

    // ============================================================
    // CreateHabitRequest → Habit (Entity)
    // ============================================================
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

    // ============================================================
    // UpdateHabitRequest → существующий Habit
    // ============================================================
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

        // Обработка категорий:
        // - categories = null → не меняем
        // - categories = пустой список → очищаем
        // - categories = список с ID → устанавливаем новые категории
        if (categories != null) {
            habit.setCategories(categories);
        }
    }

    // ============================================================
    // Habit (Entity) → HabitResponseDto
    // ============================================================
    public HabitResponseDto toResponseDto(Habit habit) {
        if (habit == null) {
            return null;
        }

        // Преобразуем список категорий в список CategoryInfo
        List<HabitResponseDto.CategoryInfo> categoryInfos = habit.getCategories().stream()
                .map(category -> new HabitResponseDto.CategoryInfo(
                        category.getId(),
                        category.getName()
                ))
                .collect(Collectors.toList());

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