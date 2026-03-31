package com.example.habittracker.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int completionCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitLog> habitLogs = new ArrayList<>();

    public Habit() {
    }

    public Habit(String name, int completionCount) {
        this.name = name;
        this.completionCount = completionCount;
    }

    public Habit(String name, int completionCount, Category category) {
        this.name = name;
        this.completionCount = completionCount;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<HabitLog> getHabitLogs() {
        return habitLogs;
    }

    public void setHabitLogs(List<HabitLog> habitLogs) {
        this.habitLogs = habitLogs;
    }
}