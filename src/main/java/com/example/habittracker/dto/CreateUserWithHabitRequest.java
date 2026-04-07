package com.example.habittracker.dto;

public class CreateUserWithHabitRequest {
    private String username;
    private String email;
    private String habitName;
    private String habitDescription;

    public CreateUserWithHabitRequest() {
        // Пустой конструктор для Jackson (десериализация JSON)
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getHabitDescription() {
        return habitDescription;
    }

    public void setHabitDescription(String habitDescription) {
        this.habitDescription = habitDescription;
    }
}