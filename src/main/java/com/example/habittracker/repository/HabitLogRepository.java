package com.example.habittracker.repository;

import com.example.habittracker.model.HabitLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    boolean existsByHabitIdAndDate(Long habitId, LocalDate date);

    @Query("""
            SELECT hl.habit.id
            FROM HabitLog hl
            WHERE hl.habit.id IN :habitIds
              AND hl.date = :date
            """)
    List<Long> findLoggedHabitIdsByHabitIdsAndDate(@Param("habitIds") List<Long> habitIds,
                                                   @Param("date") LocalDate date);
}
