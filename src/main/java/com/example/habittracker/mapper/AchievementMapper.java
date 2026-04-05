package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateAchievementRequest;
import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.dto.AchievementResponseDto;
import com.example.habittracker.model.Achievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public AchievementResponseDto toResponseDto(Achievement achievement) {
        if (achievement == null) {
            return null;
        }

        return new AchievementResponseDto(
                achievement.getId(),
                achievement.getName(),
                achievement.getCondition()
        );
    }

    public Achievement toEntity(CreateAchievementRequest request) {
        if (request == null) {
            return null;
        }

        return new Achievement(
                request.getName(),
                request.getCondition()
        );
    }

    public void updateEntity(Achievement achievement, UpdateAchievementRequest request) {
        if (request == null || achievement == null) {
            return;
        }

        if (request.getName() != null) {
            achievement.setName(request.getName());
        }

        if (request.getCondition() != null) {
            achievement.setCondition(request.getCondition());
        }
    }
}