package com.example.habittracker.dto;

public class CreateGoalRequest {
    private String name;
    private String condition;
    private Long habitId;

    public CreateGoalRequest() {
    }

    public CreateGoalRequest(String name, String condition, Long habitId) {
        this.name = name;
        this.condition = condition;
        this.habitId = habitId;
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

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }
}