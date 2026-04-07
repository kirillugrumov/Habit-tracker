package com.example.habittracker.dto;

public class CreateUserRequest {

    private String username;
    private String email;

    public CreateUserRequest() {
        // Пустой конструктор для Jackson (десериализация JSON)
    }

    public CreateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
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
}