package com.example.habittracker.repository;

import com.example.habittracker.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    boolean existsByName(String name);
}