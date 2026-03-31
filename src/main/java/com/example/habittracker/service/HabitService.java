package com.example.habittracker.service;

import com.example.habittracker.model.Category;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.CategoryRepository;
import com.example.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;

    public HabitService(HabitRepository habitRepository,
                        CategoryRepository categoryRepository) {
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Habit> getHabitById(Long id) {
        return habitRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Habit> getHabitsByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllHabits();
        }
        return habitRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Habit createHabit(Habit habit) {
        if (habitRepository.existsByName(habit.getName())) {
            throw new RuntimeException("Привычка с именем '" + habit.getName() + "' уже существует");
        }
        return habitRepository.save(habit);
    }

    @Transactional
    public Habit updateHabit(Long id, Habit updatedHabit) {
        Habit existingHabit = getHabitById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена с id: " + id));

        if (updatedHabit.getName() != null &&
                !updatedHabit.getName().equals(existingHabit.getName()) &&
                habitRepository.existsByName(updatedHabit.getName())) {
            throw new RuntimeException("Привычка с именем '" + updatedHabit.getName() + "' уже существует");
        }

        if (updatedHabit.getName() != null) {
            existingHabit.setName(updatedHabit.getName());
        }

        if (updatedHabit.getCompletionCount() >= 0) {
            existingHabit.setCompletionCount(updatedHabit.getCompletionCount());
        }

        if (updatedHabit.getCategory() != null) {
            existingHabit.setCategory(updatedHabit.getCategory());
        }

        return habitRepository.save(existingHabit);
    }

    @Transactional
    public void deleteHabit(Long id) {
        Habit habit = getHabitById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена с id: " + id));

        if (!habit.getHabitLogs().isEmpty()) {
            throw new RuntimeException("Нельзя удалить привычку, по которой есть логи выполнения");
        }

        habitRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена с id: " + categoryId));
    }
}