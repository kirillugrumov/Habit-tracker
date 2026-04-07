package com.example.habittracker.repository;

import com.example.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    boolean existsByName(String name);

    List<Habit> findAll();

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.user LEFT JOIN FETCH h.categories")
    List<Habit> findAllOptimized();
}