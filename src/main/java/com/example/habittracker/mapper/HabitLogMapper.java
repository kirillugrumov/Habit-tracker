package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.dto.UpdateHabitLogRequest;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitLog;
import com.example.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class HabitLogMapper {

    public HabitLogResponseDto toDto(HabitLog habitLog) {
        if (habitLog == null) {
            return null;
        }

        return new HabitLogResponseDto(
                habitLog.getId(),
                habitLog.getHabit() != null ? habitLog.getHabit().getId() : null,
                habitLog.getHabit() != null ? habitLog.getHabit().getName() : null,
                habitLog.getUser() != null ? habitLog.getUser().getId() : null,
                habitLog.getUser() != null ? habitLog.getUser().getUsername() : null,
                habitLog.getCompletionDate(),
                habitLog.isCompleted(),
                habitLog.getNotes()
        );
    }

    public HabitLog toEntity(CreateHabitLogRequest request, Habit habit, User user) {
        if (request == null) {
            return null;
        }

        return new HabitLog(
                habit,
                user,
                request.getCompletionDate(),
                request.isCompleted()
        );
    }

    public void updateEntity(HabitLog habitLog, UpdateHabitLogRequest request) {
        if (request == null || habitLog == null) {
            return;
        }

        if (request.getCompleted() != null) {
            habitLog.setCompleted(request.getCompleted());
        }

        if (request.getNotes() != null) {
            habitLog.setNotes(request.getNotes());
        }
    }
}