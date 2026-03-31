package com.example.habittracker.dto;

public class UpdateHabitLogRequest {

    private Boolean completed;
    private String notes;

    public UpdateHabitLogRequest() {
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}