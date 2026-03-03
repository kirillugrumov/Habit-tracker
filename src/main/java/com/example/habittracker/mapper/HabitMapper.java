package com.example.habittracker.mapper;

import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {

    public HabitResponseDto toDto(Habit habit) {
        return new HabitResponseDto(
                habit.getId(),
                habit.getName(),
                habit.getCompletionCount()
        );
    }
}