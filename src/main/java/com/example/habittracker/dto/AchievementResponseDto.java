package com.example.habittracker.dto;

public class AchievementResponseDto {
    private final Long id;
    private final String name;
    private final String condition;

    public AchievementResponseDto(Long id, String name, String condition) {
        this.id = id;
        this.name = name;
        this.condition = condition;
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
}