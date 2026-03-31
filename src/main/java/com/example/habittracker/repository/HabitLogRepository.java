package com.example.habittracker.repository;

import com.example.habittracker.model.HabitLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    // Для демонстрации N+1 проблемы - ОСТАВЛЯЕМ
    List<HabitLog> findByUserId(Long userId);

    // Решение N+1 - ОСТАВЛЯЕМ
    @EntityGraph(attributePaths = {"habit", "user"})
    @Query("SELECT hl FROM HabitLog hl WHERE hl.user.id = :userId")
    List<HabitLog> findByUserIdWithDetails(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"habit", "user"})
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.id = :habitId")
    List<HabitLog> findByHabitIdWithDetails(@Param("habitId") Long habitId);

    @EntityGraph(attributePaths = {"habit", "user"})
    @Query("SELECT hl FROM HabitLog hl WHERE hl.user.id = :userId " +
            "AND hl.completionDate BETWEEN :startDate AND :endDate")
    List<HabitLog> findByUserIdAndCompletionDateBetweenWithDetails(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = {"habit", "user"})
    @Query("SELECT hl FROM HabitLog hl " +
            "WHERE hl.user.id = :userId " +
            "AND hl.habit.id = :habitId " +
            "AND hl.completionDate = :completionDate")
    Optional<HabitLog> findByUserIdAndHabitIdAndCompletionDateWithDetails(
            @Param("userId") Long userId,
            @Param("habitId") Long habitId,
            @Param("completionDate") LocalDate completionDate);

    @Query("SELECT hl FROM HabitLog hl " +
            "JOIN FETCH hl.habit h " +
            "JOIN FETCH hl.user u " +
            "WHERE hl.completionDate = :date")
    List<HabitLog> findByCompletionDateWithFetch(@Param("date") LocalDate date);

    // Методы для статистики - ОСТАВЛЯЕМ
    long countByUserIdAndHabitIdAndCompletedTrue(Long userId, Long habitId);

    @Query("SELECT COUNT(hl) FROM HabitLog hl " +
            "WHERE hl.user.id = :userId " +
            "AND hl.completed = true " +
            "AND hl.completionDate BETWEEN :startDate AND :endDate")
    long countCompletedLogsInPeriod(@Param("userId") Long userId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}