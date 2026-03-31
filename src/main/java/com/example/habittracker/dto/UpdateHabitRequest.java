package com.example.habittracker.dto;

public class UpdateHabitRequest {

    private String name;
    private Integer completionCount;
    private Long categoryId;

    public UpdateHabitRequest() {
    }

    public UpdateHabitRequest(String name, Integer completionCount, Long categoryId) {
        this.name = name;
        this.completionCount = completionCount;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(Integer completionCount) {
        this.completionCount = completionCount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}