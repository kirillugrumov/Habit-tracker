package com.example.habittracker.mapper;

import com.example.habittracker.dto.UserWithHabitResponseDto;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserWithHabitMapper {

    public UserWithHabitResponseDto toResponseDto(User user, Habit habit) {
        if (user == null || habit == null) {
            return null;
        }

        return new UserWithHabitResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                habit.getId(),
                habit.getName(),
                habit.getDescription()
        );
    }
}