package com.example.habittracker.dto;

public class CreateHabitRequest {
    private String name;
    private String description;
    private Long userId;
    private Long categoryId;

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String name, String description, Long userId, Long categoryId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}