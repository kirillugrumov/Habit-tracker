package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for creating a user together with a habit")
public class CreateUserWithHabitRequest {
    @Schema(description = "Username", example = "anna")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "User email", example = "anna@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Habit name", example = "Read 20 pages")
    @NotBlank(message = "Habit name is required")
    private String habitName;

    @Schema(description = "Habit description", example = "Read every evening before sleep")
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
