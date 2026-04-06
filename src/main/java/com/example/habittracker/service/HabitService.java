package com.example.habittracker.service;

import com.example.habittracker.dto.CreateHabitRequest;
import com.example.habittracker.dto.UpdateHabitRequest;
import com.example.habittracker.dto.HabitResponseDto;
import com.example.habittracker.mapper.HabitMapper;
import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.CategoryRepository;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
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

    public HabitService(HabitRepository habitRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        HabitMapper habitMapper) {
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.habitMapper = habitMapper;
    }

    @Transactional
    public HabitResponseDto createHabit(CreateHabitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + request.getUserId()));

        if (habitRepository.existsByName(request.getName())) {
            throw new RuntimeException("Привычка с именем '" + request.getName() + "' уже существует");
        }

        // ✅ Изменено: categoryId → categoryIds (список)
        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new RuntimeException("Некоторые категории не найдены");
            }
        }

        // ✅ Изменено: передаём список категорий вместо одной
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
                throw new RuntimeException("Имя '" + request.getName() + "' уже занято");
            }
            habit.setName(request.getName());
        }

        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }

        // ✅ Изменено: categoryId → categoryIds (список)
        List<Category> categories = null;
        if (request.getCategoryIds() != null) {
            if (request.getCategoryIds().isEmpty()) {
                // Пустой список → очистить категории
                categories = new ArrayList<>();
            } else {
                categories = categoryRepository.findAllById(request.getCategoryIds());
                if (categories.size() != request.getCategoryIds().size()) {
                    throw new RuntimeException("Некоторые категории не найдены");
                }
            }
        }

        // ✅ Изменено: передаём список категорий
        habitMapper.updateEntity(habit, request, categories);

        Habit updatedHabit = habitRepository.save(habit);
        return habitMapper.toResponseDto(updatedHabit);
    }

    @Transactional
    public void deleteHabit(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new RuntimeException("Привычка не найдена с id: " + id);
        }
        habitRepository.deleteById(id);
    }

    private Habit getHabitByIdEntity(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена с id: " + id));
    }
}