package com.example.habittracker.service;

import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.model.Achievement;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.AchievementRepository;
import com.example.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievementsWithUsers() {
        return achievementRepository.findAllWithUsers();
    }

    @Transactional(readOnly = true)
    public Achievement getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Достижение не найдено с id: " + id));
    }

    @Transactional(readOnly = true)
    public Achievement getAchievementByIdWithUsers(Long id) {
        return achievementRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new RuntimeException("Достижение не найдено с id: " + id));
    }

    @Transactional
    public Achievement createAchievement(Achievement achievement) {
        if (achievementRepository.existsByName(achievement.getName())) {
            throw new RuntimeException("Достижение с именем '" + achievement.getName() + "' уже существует");
        }
        return achievementRepository.save(achievement);
    }

    @Transactional
    public Achievement updateAchievement(Long id, UpdateAchievementRequest request) {
        Achievement achievement = getAchievementById(id);

        if (request.getName() != null) {
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

        if (request.getName() != null && achievementRepository.existsByName(request.getName())) {
            throw new RuntimeException("Достижение с именем '" + request.getName() + "' уже существует");
        }
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new RuntimeException("Достижение не найдено с id: " + id);
        }
        achievementRepository.deleteById(id);
    }

    @Transactional
    public void assignAchievementToUser(Long userId, Long achievementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));
        Achievement achievement = getAchievementById(achievementId);

        if (user.getAchievements().contains(achievement)) {
            throw new RuntimeException("Достижение уже назначено пользователю");
        }

        user.getAchievements().add(achievement);
        userRepository.save(user);
    }

    @Transactional
    public void removeAchievementFromUser(Long userId, Long achievementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));
        Achievement achievement = getAchievementById(achievementId);

        if (!user.getAchievements().contains(achievement)) {
            throw new RuntimeException("Достижение не назначено пользователю");
        }

        user.getAchievements().remove(achievement);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));
        return user.getAchievements();
    }

    @Transactional
    public void checkAndAssignAchievements(Long userId, int currentStreak) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));

        List<Achievement> availableAchievements =
                achievementRepository.findByRequiredStreakLessThanEqual(currentStreak);

        for (Achievement achievement : availableAchievements) {
            if (!user.getAchievements().contains(achievement)) {
                user.getAchievements().add(achievement);
            }
        }

        userRepository.save(user);
    }
}