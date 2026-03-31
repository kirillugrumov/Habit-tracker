package com.example.habittracker.repository;

import com.example.habittracker.model.Achievement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Optional<Achievement> findByName(String name);

    Optional<Achievement> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    List<Achievement> findByRequiredStreakLessThanEqual(int streak);

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT a FROM Achievement a")
    List<Achievement> findAllWithUsers();

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT a FROM Achievement a WHERE a.id = :id")
    Optional<Achievement> findByIdWithUsers(@Param("id") Long id);

}