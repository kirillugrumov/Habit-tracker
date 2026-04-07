package com.example.habittracker.dto;

import java.util.List;

public class UpdateHabitRequest {
    private String name;
    private String description;
    private List<Long> categoryIds;

    public UpdateHabitRequest() {
    }

    public UpdateHabitRequest(String name, String description, List<Long> categoryIds) {
        this.name = name;
        this.description = description;
        this.categoryIds = categoryIds;
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

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}