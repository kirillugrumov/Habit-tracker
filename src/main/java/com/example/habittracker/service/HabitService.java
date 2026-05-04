package com.example.habittracker.service;

import com.example.habittracker.cache.HabitSearchCache;
import com.example.habittracker.cache.HabitSearchCacheKey;
import com.example.habittracker.cache.HabitSearchQueryType;
import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.UserWithHabitResponseDto;
import com.example.habittracker.mapper.HabitMapper;
import com.example.habittracker.mapper.UserWithHabitMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.CategoryRepository;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;
    private final UserWithHabitMapper userWithHabitMapper;
    private final HabitSearchCache habitSearchCache;

    public HabitService(HabitRepository habitRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        HabitMapper habitMapper,
                        UserWithHabitMapper userWithHabitMapper,
                        HabitSearchCache habitSearchCache) {
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.habitMapper = habitMapper;
        this.userWithHabitMapper = userWithHabitMapper;
        this.habitSearchCache = habitSearchCache;
    }

    @Transactional
    public HabitResponseDto createHabit(CreateHabitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        if (habitRepository.existsByName(request.getName())) {
            throw new EntityExistsException("Habit with name '" + request.getName() + "' already exists");
        }

        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new EntityNotFoundException("Some categories were not found");
            }
        }

        Habit habit = habitMapper.toEntity(request, categories);
        habit.setUser(user);

        Habit savedHabit = habitRepository.save(habit);
        invalidateSearchCache();

        return habitMapper.toResponseDto(savedHabit);
    }

    @Transactional(readOnly = true)
    public List<HabitResponseDto> getAllHabits() {
        List<Habit> habits = habitRepository.findAll();
        return habits.stream()
                .map(habitMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HabitResponseDto getHabitById(Long id) {
        Habit habit = getHabitByIdEntity(id);
        return habitMapper.toResponseDto(habit);
    }

    @Transactional
    public HabitResponseDto updateHabit(Long id, UpdateHabitRequest request) {
        Habit habit = getHabitByIdEntity(id);

        if (request.getName() != null && !request.getName().equals(habit.getName())) {
            if (habitRepository.existsByName(request.getName())) {
                throw new EntityExistsException("Name '" + request.getName() + "' is already taken");
            }
            habit.setName(request.getName());
        }

        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }

        List<Category> categories = null;
        if (request.getCategoryIds() != null) {
            if (request.getCategoryIds().isEmpty()) {
                categories = new ArrayList<>();
            } else {
                categories = categoryRepository.findAllById(request.getCategoryIds());
                if (categories.size() != request.getCategoryIds().size()) {
                    throw new EntityNotFoundException("Some categories were not found");
                }
            }
        }

        habitMapper.updateEntity(habit, request, categories);

        Habit updatedHabit = habitRepository.save(habit);
        invalidateSearchCache();
        return habitMapper.toResponseDto(updatedHabit);
    }

    @Transactional
    public void deleteHabit(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new EntityNotFoundException("Habit not found with id: " + id);
        }
        habitRepository.deleteById(id);
        invalidateSearchCache();
    }

    @SuppressWarnings("java:S4144")
    @Transactional(readOnly = true)
    public List<HabitResponseDto> getHabitsWithProblem() {
        List<Habit> habits = habitRepository.findAll();
        return habits.stream()
                .map(habitMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HabitResponseDto> getHabitsOptimized() {
        List<Habit> habits = habitRepository.findAllOptimized();
        return habits.stream()
                .map(habitMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<HabitResponseDto> searchHabitsByUserAndCategoryJpql(
            String username,
            String categoryName,
            Pageable pageable) {
        return searchHabits(username, categoryName, pageable, HabitSearchQueryType.JPQL);
    }

    @Transactional(readOnly = true)
    public Page<HabitResponseDto> searchHabitsByUserAndCategoryNative(
            String username,
            String categoryName,
            Pageable pageable) {
        return searchHabits(username, categoryName, pageable, HabitSearchQueryType.NATIVE);
    }

    public UserWithHabitResponseDto saveUserAndHabitWithoutTransaction(
            String username, String email, String habitName, String habitDescription) {
        User user = new User(username, email);
        User savedUser = userRepository.save(user);

        Habit habit = new Habit(habitName, savedUser);
        habit.setDescription(habitDescription);
        Habit savedHabit = habitRepository.save(habit);
        invalidateSearchCache();

        return userWithHabitMapper.toResponseDto(savedUser, savedHabit);
    }

    @Transactional
    public UserWithHabitResponseDto saveUserAndHabitWithTransaction(
            String username, String email, String habitName, String habitDescription) {
        return saveUserAndHabitWithoutTransaction(username, email, habitName, habitDescription);
    }

    private Habit getHabitByIdEntity(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + id));
    }

    private Page<HabitResponseDto> searchHabits(String username,
                                                String categoryName,
                                                Pageable pageable,
                                                HabitSearchQueryType queryType) {
        String normalizedUsername = normalizeFilter(username);
        String normalizedCategoryName = normalizeFilter(categoryName);
        HabitSearchCacheKey cacheKey = new HabitSearchCacheKey(
                queryType,
                normalizedUsername,
                normalizedCategoryName,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );

        Page<HabitResponseDto> cachedPage = habitSearchCache.get(cacheKey);
        if (cachedPage != null) {
            return cachedPage;
        }

        Page<Habit> habitsPage = queryType == HabitSearchQueryType.JPQL
                ? findHabitsPageWithoutNPlusOne(normalizedUsername, normalizedCategoryName, pageable)
                : habitRepository.searchByUserAndCategoryNative(normalizedUsername, normalizedCategoryName, pageable);

        Page<HabitResponseDto> responsePage = habitsPage.map(habitMapper::toResponseDto);
        habitSearchCache.put(cacheKey, responsePage);
        return responsePage;
    }

    private String normalizeFilter(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private void invalidateSearchCache() {
        habitSearchCache.invalidateAll();
    }

    private Page<Habit> findHabitsPageWithoutNPlusOne(String username,
                                                      String categoryName,
                                                      Pageable pageable) {
        Page<Long> habitIdsPage = habitRepository.findHabitIdsByUserAndCategoryJpql(
                username,
                categoryName,
                pageable
        );

        if (habitIdsPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, habitIdsPage.getTotalElements());
        }

        List<Long> habitIds = habitIdsPage.getContent();
        List<Habit> fetchedHabits = habitRepository.findAllWithUserAndCategoriesByIdIn(habitIds);

        Map<Long, Habit> habitsById = new LinkedHashMap<>();
        for (Habit habit : fetchedHabits) {
            habitsById.put(habit.getId(), habit);
        }

        List<Habit> orderedHabits = habitIds.stream()
                .map(habitsById::get)
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(orderedHabits, pageable, habitIdsPage.getTotalElements());
    }
}
