package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateHabitLogRequest;
import com.example.habittracker.dto.HabitLogResponseDto;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitLog;
import org.springframework.stereotype.Component;

@Component
public class HabitLogMapper {

    public HabitLogResponseDto toResponseDto(HabitLog habitLog) {
        if (habitLog == null) {
            return null;
        }

        return new HabitLogResponseDto(
                habitLog.getId(),
                habitLog.getHabit() != null ? habitLog.getHabit().getId() : null,
                habitLog.getLogDate()
        );
    }

    public HabitLog toEntity(CreateHabitLogRequest request, Habit habit) {
        if (request == null) {
            return null;
        }

        return new HabitLog(habit, null); // logDate установится в сервисе
    }

}