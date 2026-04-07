package com.example.habittracker.dto;

public class UserWithHabitResponseDto {
    private final Long userId;
    private final String username;
    private final String email;
    private final Long habitId;
    private final String habitName;
    private final String habitDescription;

    public UserWithHabitResponseDto(Long userId, String username, String email,
                                    Long habitId, String habitName, String habitDescription) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.habitId = habitId;
        this.habitName = habitName;
        this.habitDescription = habitDescription;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Long getHabitId() {
        return habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public String getHabitDescription() {
        return habitDescription;
    }
}