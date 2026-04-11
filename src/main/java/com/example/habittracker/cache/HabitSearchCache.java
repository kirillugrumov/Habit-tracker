package com.example.habittracker.cache;

import com.example.habittracker.dto.HabitResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HabitSearchCache {

    private final Map<HabitSearchCacheKey, Page<HabitResponseDto>> cache = new HashMap<>();

    public synchronized Page<HabitResponseDto> get(HabitSearchCacheKey key) {
        return cache.get(key);
    }

    public synchronized void put(HabitSearchCacheKey key, Page<HabitResponseDto> value) {
        cache.put(key, value);
    }

    public synchronized void invalidateAll() {
        cache.clear();
    }
}
