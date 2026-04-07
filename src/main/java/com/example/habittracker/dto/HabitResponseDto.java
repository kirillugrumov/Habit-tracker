package com.example.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class HabitResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private String username;
    private List<CategoryInfo> categories;

    @Data
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }
}