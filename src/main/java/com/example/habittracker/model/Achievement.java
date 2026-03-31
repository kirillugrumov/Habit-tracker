package com.example.habittracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "required_streak")
    private int requiredStreak;

    @Column(name = "icon")
    private String icon;

    @ManyToMany(mappedBy = "achievements", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    public Achievement() {
    }

    public Achievement(String name, String description, int requiredStreak, String icon) {
        this.name = name;
        this.description = description;
        this.requiredStreak = requiredStreak;
        this.icon = icon;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequiredStreak() {
        return requiredStreak;
    }

    public void setRequiredStreak(int requiredStreak) {
        this.requiredStreak = requiredStreak;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}