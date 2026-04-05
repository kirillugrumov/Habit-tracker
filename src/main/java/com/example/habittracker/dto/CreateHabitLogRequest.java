package com.example.habittracker.dto;

public class CreateHabitLogRequest {
    private Long habitId;

    public CreateHabitLogRequest() {
    }

    public CreateHabitLogRequest(Long habitId) {
        this.habitId = habitId;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }
}