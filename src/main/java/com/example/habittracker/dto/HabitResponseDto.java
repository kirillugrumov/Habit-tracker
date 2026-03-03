package com.example.habittracker.dto;

public class HabitResponseDto {

    private Long id;
    private String name;
    private int completionCount;

    public HabitResponseDto(Long id, String name, int completionCount) {
        this.id = id;
        this.name = name;
        this.completionCount = completionCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCompletionCount() {
        return completionCount;
    }
}