package com.example.habittracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Habit> habits = new ArrayList<>();

    public Category(String name, String description) {
        this.setName(name);
        this.description = description;
    }
}