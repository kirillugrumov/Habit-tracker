package com.example.habittracker.repository;

import com.example.habittracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Проверка существования категории по имени (для createCategory и updateCategory)
    boolean existsByName(String name);
}