package com.example.habittracker.dto;

public class UpdateAchievementRequest {

    private String name;
    private String description;
    private Integer requiredStreak;
    private String icon;

    public UpdateAchievementRequest() {
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

    public Integer getRequiredStreak() {
        return requiredStreak;
    }

    public void setRequiredStreak(Integer requiredStreak) {
        this.requiredStreak = requiredStreak;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}