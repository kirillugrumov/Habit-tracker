package com.example.habittracker.repository;

import com.example.habittracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM habit_categories WHERE category_id = :categoryId", nativeQuery = true)
    void deleteCategoryLinks(@Param("categoryId") Long categoryId);
}