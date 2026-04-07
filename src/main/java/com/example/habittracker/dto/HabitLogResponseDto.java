package com.example.habittracker.dto;

import java.time.LocalDate;

public class HabitLogResponseDto {
    private final Long id;
    private final Long habitId;
    private final LocalDate date;

    public HabitLogResponseDto(Long id, Long habitId, LocalDate date) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public LocalDate getLogDate() {
        return date;
    }
}