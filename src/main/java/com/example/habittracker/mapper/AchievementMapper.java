package com.example.habittracker.mapper;

import com.example.habittracker.dto.AchievementResponseDto;
import com.example.habittracker.dto.CreateAchievementRequest;
import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.model.Achievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public AchievementResponseDto toDto(Achievement achievement) {
        if (achievement == null) {
            return null;
        }

        int usersCount = achievement.getUsers() != null ? achievement.getUsers().size() : 0;

        return new AchievementResponseDto(
                achievement.getId(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getRequiredStreak(),
                achievement.getIcon(),
                usersCount
        );
    }

    public Achievement toEntity(CreateAchievementRequest request) {
        if (request == null) {
            return null;
        }

        return new Achievement(
                request.getName(),
                request.getDescription(),
                request.getRequiredStreak(),
                request.getIcon()
        );
    }

    public void updateEntity(Achievement achievement, UpdateAchievementRequest request) {
        if (request == null || achievement == null) {
            return;
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            achievement.setName(request.getName());
        }

        if (request.getDescription() != null) {
            achievement.setDescription(request.getDescription());
        }

        if (request.getRequiredStreak() != null) {
            achievement.setRequiredStreak(request.getRequiredStreak());
        }

        if (request.getIcon() != null) {
            achievement.setIcon(request.getIcon());
        }
    }
}