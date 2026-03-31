package com.example.habittracker.service;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitLog;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.HabitLogRepository;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitLogService(HabitLogRepository habitLogRepository,
                           HabitRepository habitRepository,
                           UserRepository userRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public HabitLog getHabitLogById(Long id) {
        return habitLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Лог не найден"));
    }

    @Transactional(readOnly = true)
    public List<HabitLog> getHabitLogsByUserIdWithDetails(Long userId) {
        return habitLogRepository.findByUserIdWithDetails(userId);
    }

    @Transactional
    public HabitLog createHabitLog(Long habitId, Long userId, LocalDate completionDate, boolean completed) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        HabitLog habitLog = new HabitLog(habit, user, completionDate, completed);

        if (completed) {
            habit.setCompletionCount(habit.getCompletionCount() + 1);
            habitRepository.save(habit);
        }

        return habitLogRepository.save(habitLog);
    }

    @Transactional
    public HabitLog updateHabitLog(Long id, Boolean completed, String notes) {
        HabitLog habitLog = getHabitLogById(id);

        if (completed != null) {
            boolean oldCompleted = habitLog.isCompleted();
            habitLog.setCompleted(completed);

            if (completed && !oldCompleted) {
                Habit habit = habitLog.getHabit();
                habit.setCompletionCount(habit.getCompletionCount() + 1);
                habitRepository.save(habit);
            } else if (!completed && oldCompleted) {
                Habit habit = habitLog.getHabit();
                habit.setCompletionCount(habit.getCompletionCount() - 1);
                habitRepository.save(habit);
            }
        }

        if (notes != null) {
            habitLog.setNotes(notes);
        }

        return habitLogRepository.save(habitLog);
    }

    @Transactional
    public void deleteHabitLog(Long id) {
        HabitLog habitLog = getHabitLogById(id);

        if (habitLog.isCompleted()) {
            Habit habit = habitLog.getHabit();
            habit.setCompletionCount(habit.getCompletionCount() - 1);
            habitRepository.save(habit);
        }

        habitLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void demonstrateNPlusOneProblem(Long userId) {
        List<HabitLog> logs = habitLogRepository.findByUserId(userId);
        for (HabitLog log : logs) {
            log.getHabit().getName();
            log.getUser().getUsername();
        }
    }

    @Transactional(readOnly = true)
    public void demonstrateSolutionWithEntityGraph(Long userId) {
        List<HabitLog> logs = habitLogRepository.findByUserIdWithDetails(userId);
        for (HabitLog log : logs) {
            log.getHabit().getName();
            log.getUser().getUsername();
        }
    }

    public void saveWithoutTransaction() {
        Habit habit = habitRepository.findById(1L).orElseThrow();
        User user = userRepository.findById(1L).orElseThrow();

        habitLogRepository.save(new HabitLog(habit, user, LocalDate.now(), true));
        habitLogRepository.save(new HabitLog(habit, user, LocalDate.now().plusDays(1), true));

        throw new RuntimeException("Ошибка - первые два сохранены");
    }

    @Transactional
    public void saveWithTransaction() {
        Habit habit = habitRepository.findById(1L).orElseThrow();
        User user = userRepository.findById(1L).orElseThrow();

        habitLogRepository.save(new HabitLog(habit, user, LocalDate.now(), true));
        habitLogRepository.save(new HabitLog(habit, user, LocalDate.now().plusDays(1), true));

        throw new RuntimeException("Ошибка - все откатится");
    }

    @Transactional(readOnly = true)
    public List<HabitLog> getHabitLogsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return habitLogRepository.findByUserIdAndCompletionDateBetweenWithDetails(userId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<HabitLog> getHabitLogsByHabitIdWithDetails(Long habitId) {
        return habitLogRepository.findByHabitIdWithDetails(habitId);
    }
}