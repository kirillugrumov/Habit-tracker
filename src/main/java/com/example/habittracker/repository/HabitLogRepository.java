package com.example.habittracker.repository;

import com.example.habittracker.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

}