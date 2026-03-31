package com.example.habittracker.dto;

import java.time.LocalDate;

public class HabitLogResponseDto {

    private Long id;
    private Long habitId;
    private String habitName;
    private Long userId;
    private String username;
    private LocalDate completionDate;
    private boolean completed;
    private String notes;

    public HabitLogResponseDto() {
    }

    public HabitLogResponseDto(Long id, Long habitId, String habitName, Long userId,
                               String username, LocalDate completionDate, boolean completed,
                               String notes) {
        this.id = id;
        this.habitId = habitId;
        this.habitName = habitName;
        this.userId = userId;
        this.username = username;
        this.completionDate = completionDate;
        this.completed = completed;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}