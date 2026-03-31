package com.example.habittracker.controller;

import com.example.habittracker.dto.AchievementResponseDto;
import com.example.habittracker.dto.CreateAchievementRequest;
import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.dto.UserAchievementDto;
import com.example.habittracker.mapper.AchievementMapper;
import com.example.habittracker.model.Achievement;
import com.example.habittracker.service.AchievementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementMapper achievementMapper;

    public AchievementController(AchievementService achievementService,
                                 AchievementMapper achievementMapper) {
        this.achievementService = achievementService;
        this.achievementMapper = achievementMapper;
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponseDto>> getAllAchievements() {
        List<Achievement> achievements = achievementService.getAllAchievements();
        List<AchievementResponseDto> response = achievements.stream()
                .map(achievementMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-users")
    public ResponseEntity<List<AchievementResponseDto>> getAllAchievementsWithUsers() {
        List<Achievement> achievements = achievementService.getAllAchievementsWithUsers();
        List<AchievementResponseDto> response = achievements.stream()
                .map(achievementMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponseDto> getAchievementById(@PathVariable Long id) {
        Achievement achievement = achievementService.getAchievementById(id);
        return ResponseEntity.ok(achievementMapper.toDto(achievement));
    }

    @GetMapping("/{id}/with-users")
    public ResponseEntity<AchievementResponseDto> getAchievementByIdWithUsers(@PathVariable Long id) {
        Achievement achievement = achievementService.getAchievementByIdWithUsers(id);
        return ResponseEntity.ok(achievementMapper.toDto(achievement));
    }

    @PostMapping
    public ResponseEntity<AchievementResponseDto> createAchievement(
            @RequestBody CreateAchievementRequest request) {
        Achievement achievement = achievementMapper.toEntity(request);
        Achievement savedAchievement = achievementService.createAchievement(achievement);
        return new ResponseEntity<>(achievementMapper.toDto(savedAchievement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementResponseDto> updateAchievement(
            @PathVariable Long id,
            @RequestBody UpdateAchievementRequest request) {
        Achievement updatedAchievement = achievementService.updateAchievement(id, request);
        return ResponseEntity.ok(achievementMapper.toDto(updatedAchievement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignAchievementToUser(@RequestBody UserAchievementDto dto) {
        achievementService.assignAchievementToUser(dto.getUserId(), dto.getAchievementId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/assign")
    public ResponseEntity<Void> removeAchievementFromUser(@RequestBody UserAchievementDto dto) {
        achievementService.removeAchievementFromUser(dto.getUserId(), dto.getAchievementId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementResponseDto>> getUserAchievements(@PathVariable Long userId) {
        List<Achievement> achievements = achievementService.getUserAchievements(userId);
        List<AchievementResponseDto> response = achievements.stream()
                .map(achievementMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }
}