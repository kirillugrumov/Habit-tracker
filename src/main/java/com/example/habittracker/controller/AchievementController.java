package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateAchievementRequest;
import com.example.habittracker.dto.UpdateAchievementRequest;
import com.example.habittracker.dto.AchievementResponseDto;
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

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping
    public ResponseEntity<AchievementResponseDto> createAchievement(@RequestBody CreateAchievementRequest request) {
        AchievementResponseDto savedAchievement = achievementService.createAchievement(request);
        return new ResponseEntity<>(savedAchievement, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponseDto>> getAllAchievements() {
        List<AchievementResponseDto> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponseDto> getAchievementById(@PathVariable Long id) {
        AchievementResponseDto achievement = achievementService.getAchievementById(id);
        return ResponseEntity.ok(achievement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementResponseDto> updateAchievement(
            @PathVariable Long id,
            @RequestBody UpdateAchievementRequest request) {
        AchievementResponseDto updatedAchievement = achievementService.updateAchievement(id, request);
        return ResponseEntity.ok(updatedAchievement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }
}