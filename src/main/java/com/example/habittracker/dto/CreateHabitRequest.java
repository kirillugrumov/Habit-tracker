package com.example.habittracker.dto;

public class CreateHabitRequest {

    private String name;
    private Integer completionCount; // опционально, по умолчанию 0
    private Long categoryId; // опционально

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String name, Integer completionCount, Long categoryId) {
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