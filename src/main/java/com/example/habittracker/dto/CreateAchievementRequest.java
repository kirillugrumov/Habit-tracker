package com.example.habittracker.dto;

public class CreateAchievementRequest {

    private String name;
    private String description;
    private int requiredStreak;
    private String icon;

    public CreateAchievementRequest() {
    }

    public CreateAchievementRequest(String name, String description, int requiredStreak, String icon) {
        this.name = name;
        this.description = description;
        this.requiredStreak = requiredStreak;
        this.icon = icon;
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
}