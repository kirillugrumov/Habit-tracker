package com.example.habittracker.repository;

import com.example.habittracker.model.Habit;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HabitRepository {

    private final List<Habit> habits = new ArrayList<>();

    public HabitRepository() {
        habits.add(new Habit(1L, "Drink Water", 5));
        habits.add(new Habit(2L, "Read Book", 3));
        habits.add(new Habit(3L, "Morning Exercise", 7));
        habits.add(new Habit(4L, "Meditate", 10));
        habits.add(new Habit(5L, "Learn Java", 2));
    }

    public List<Habit> findAll() {
        return habits;
    }

    public Optional<Habit> findById(Long id) {
        return habits.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    public List<Habit> findByName(String name) {
        return habits.stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .toList();
    }
}