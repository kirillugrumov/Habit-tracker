package com.example.habittracker.dto;

public class GoalResponseDto {
    private final Long id;
    private final String name;
    private final String condition;
    private final Long habitId;
    private final String habitName;

    public GoalResponseDto(Long id, String name, String condition, Long habitId, String habitName) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.habitId = habitId;
        this.habitName = habitName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public Long getHabitId() {
        return habitId;
    }

    public String getHabitName() {
        return habitName;
    }
}