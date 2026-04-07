package com.example.habittracker.dto;

import java.util.ArrayList;
import java.util.List;

public class CreateHabitRequest {
    private String name;
    private String description;
    private Long userId;
    private List<Long> categoryIds = new ArrayList<>();

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String name, String description, Long userId, List<Long> categoryIds) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
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

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
    }
}