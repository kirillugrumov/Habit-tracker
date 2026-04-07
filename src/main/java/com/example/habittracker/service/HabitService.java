package com.example.habittracker.service;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;
    private final UserWithHabitMapper userWithHabitMapper;

    public HabitService(HabitRepository habitRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        HabitMapper habitMapper,
                        UserWithHabitMapper userWithHabitMapper) {
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.habitMapper = habitMapper;
        this.userWithHabitMapper = userWithHabitMapper;
    }

    @Transactional
    public HabitResponseDto createHabit(CreateHabitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + request.getUserId()));

        if (habitRepository.existsByName(request.getName())) {
            throw new EntityExistsException("Привычка с именем '" + request.getName() + "' уже существует");
        }

        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new EntityNotFoundException("Некоторые категории не найдены");
            }
        }

        Habit habit = habitMapper.toEntity(request, categories);
        habit.setUser(user);

        Habit savedHabit = habitRepository.save(habit);

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
                throw new EntityExistsException("Имя '" + request.getName() + "' уже занято");
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
                    throw new EntityNotFoundException("Некоторые категории не найдены");
                }
            }
        }

        habitMapper.updateEntity(habit, request, categories);

        Habit updatedHabit = habitRepository.save(habit);
        return habitMapper.toResponseDto(updatedHabit);
    }

    @Transactional
    public void deleteHabit(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new EntityNotFoundException("Привычка не найдена с id: " + id);
        }
        habitRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<HabitResponseDto> getHabitsWithProblem() {
        // NOSONAR - метод для демонстрации проблемы N+1
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

    public UserWithHabitResponseDto saveUserAndHabitWithoutTransaction(
            String username, String email, String habitName, String habitDescription) {
        User user = new User(username, email);
        User savedUser = userRepository.save(user);

        Habit habit = new Habit(habitName, savedUser);
        habit.setDescription(habitDescription);
        Habit savedHabit = habitRepository.save(habit);

        return userWithHabitMapper.toResponseDto(savedUser, savedHabit);
    }

    @Transactional
    public UserWithHabitResponseDto saveUserAndHabitWithTransaction(
            String username, String email, String habitName, String habitDescription) {
        return saveUserAndHabitWithoutTransaction(username, email, habitName, habitDescription);
    }

    private Habit getHabitByIdEntity(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена с id: " + id));
    }
}