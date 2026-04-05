package com.example.habittracker.service;

import com.example.habittracker.dto.CreateAchievementRequest;
import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.dto.AchievementResponseDto;
import com.example.habittracker.mapper.AchievementMapper;
import com.example.habittracker.model.Achievement;
import com.example.habittracker.repository.AchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementMapper achievementMapper;

    public AchievementService(AchievementRepository achievementRepository,
                              AchievementMapper achievementMapper) {
        this.achievementRepository = achievementRepository;
        this.achievementMapper = achievementMapper;
    }

    @Transactional
    public AchievementResponseDto createAchievement(CreateAchievementRequest request) {
        if (achievementRepository.existsByName(request.getName())) {
            throw new RuntimeException("Достижение с именем '" + request.getName() + "' уже существует");
        }
        Achievement achievement = achievementMapper.toEntity(request);
        Achievement savedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toResponseDto(savedAchievement);
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDto> getAllAchievements() {
        List<Achievement> achievements = achievementRepository.findAll();

        return achievements.stream()
                .map(achievementMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AchievementResponseDto getAchievementById(Long id) {
        Achievement achievement = getAchievementByIdEntity(id);
        return achievementMapper.toResponseDto(achievement);
    }

    @Transactional
    public AchievementResponseDto updateAchievement(Long id, UpdateAchievementRequest request) {
        Achievement achievement = getAchievementByIdEntity(id);

        if (request.getName() != null && !request.getName().equals(achievement.getName())) {
            if (achievementRepository.existsByName(request.getName())) {
                throw new RuntimeException("Имя '" + request.getName() + "' уже занято");
            }
            achievement.setName(request.getName());
        }

        if (request.getCondition() != null) {
            achievement.setCondition(request.getCondition());
        }

        Achievement updatedAchievement = achievementRepository.save(achievement);
        return achievementMapper.toResponseDto(updatedAchievement);
    }

    @Transactional
    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new RuntimeException("Достижение не найдено с id: " + id);
        }
        achievementRepository.deleteById(id);
    }

    private Achievement getAchievementByIdEntity(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Достижение не найдено с id: " + id));
    }
}