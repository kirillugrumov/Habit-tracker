package com.example.habittracker.dto;

public class HabitResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Long userId;
    private final String username;
    private final Long categoryId;
    private final String categoryName;

    public HabitResponseDto(Long id, String name, String description,
                            Long userId, String username,
                            Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.username = username;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}