package com.example.habittracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "goals")
public class Goal extends BaseEntity {

    private String condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    public Goal(String name, String condition, Habit habit) {
        this.setName(name);
        this.condition = condition;
        this.habit = habit;
    }
}