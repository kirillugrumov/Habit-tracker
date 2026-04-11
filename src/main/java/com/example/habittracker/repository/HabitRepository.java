package com.example.habittracker.repository;

import com.example.habittracker.model.Habit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    boolean existsByName(String name);

    List<Habit> findAll();

    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.user LEFT JOIN FETCH h.categories")
    List<Habit> findAllOptimized();

    @Query(
            value = """
                    SELECT DISTINCT h
                    FROM Habit h
                    JOIN h.user u
                    LEFT JOIN h.categories c
                    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
                      AND LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT h)
                    FROM Habit h
                    JOIN h.user u
                    LEFT JOIN h.categories c
                    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
                      AND LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                    """
    )
    Page<Habit> searchByUserAndCategoryJpql(@Param("username") String username,
                                            @Param("categoryName") String categoryName,
                                            Pageable pageable);

    @Query(
            value = """
                    SELECT DISTINCT h.*
                    FROM habits h
                    JOIN users u ON u.id = h.user_id
                    LEFT JOIN habit_categories hc ON hc.habit_id = h.id
                    LEFT JOIN categories c ON c.id = hc.category_id
                    WHERE u.username ILIKE CONCAT('%', :username, '%')
                      AND COALESCE(c.name, '') ILIKE CONCAT('%', :categoryName, '%')
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT h.id)
                    FROM habits h
                    JOIN users u ON u.id = h.user_id
                    LEFT JOIN habit_categories hc ON hc.habit_id = h.id
                    LEFT JOIN categories c ON c.id = hc.category_id
                    WHERE u.username ILIKE CONCAT('%', :username, '%')
                      AND COALESCE(c.name, '') ILIKE CONCAT('%', :categoryName, '%')
                    """,
            nativeQuery = true
    )
    Page<Habit> searchByUserAndCategoryNative(@Param("username") String username,
                                              @Param("categoryName") String categoryName,
                                              Pageable pageable);
}
