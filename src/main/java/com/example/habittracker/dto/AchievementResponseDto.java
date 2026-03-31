package com.example.habittracker.dto;

public class AchievementResponseDto {

    private Long id;
    private String name;
    private String description;
    private int requiredStreak;
    private String icon;
    private int usersCount;

    public AchievementResponseDto() {
    }

    public AchievementResponseDto(Long id, String name, String description,
                                  int requiredStreak, String icon, int usersCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredStreak = requiredStreak;
        this.icon = icon;
        this.usersCount = usersCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequiredStreak() {
        return requiredStreak;
    }

    public void setRequiredStreak(int requiredStreak) {
        this.requiredStreak = requiredStreak;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }
}