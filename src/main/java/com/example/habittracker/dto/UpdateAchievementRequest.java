package com.example.habittracker.dto;

public class UpdateAchievementRequest {
    private String name;
    private String condition;

    public UpdateAchievementRequest() {
    }

    public UpdateAchievementRequest(String name, String condition) {
        this.name = name;
        this.condition = condition;
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