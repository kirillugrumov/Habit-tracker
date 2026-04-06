package com.example.habittracker.dto;

import java.util.List;

public class HabitResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Long userId;
    private final String username;
    private final List<CategoryInfo> categories;  // ✅ изменено

    // ✅ Внутренний DTO для категории
    public static class CategoryInfo {
        private final Long id;
        private final String name;

        public CategoryInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // ✅ Исправлен конструктор
    public HabitResponseDto(Long id, String name, String description,
                            Long userId, String username,
                            List<CategoryInfo> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.username = username;
        this.categories = categories != null ? categories : List.of();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    // ✅ Вместо getCategoryId/getCategoryName
    public List<CategoryInfo> getCategories() {
        return categories;
    }
}