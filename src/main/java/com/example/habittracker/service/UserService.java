package com.example.habittracker.service;

import com.example.habittracker.model.User;
import com.example.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = new User(username, email);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с username: " + username));
    }

    @Transactional
    public User updateUser(Long id, String newUsername, String newEmail) {
        User user = getUserById(id);

        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new RuntimeException("Имя пользователя уже занято"); // Исправлено
            }
            user.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new RuntimeException("Email уже занят");
            }
            user.setEmail(newEmail);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Пользователь не найден с id: " + id);
        }
        userRepository.deleteById(id);
    }
}