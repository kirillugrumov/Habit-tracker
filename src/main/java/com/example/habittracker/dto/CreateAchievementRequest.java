package com.example.habittracker.dto;

public class CreateAchievementRequest {
    private String name;
    private String condition;

    public CreateAchievementRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}