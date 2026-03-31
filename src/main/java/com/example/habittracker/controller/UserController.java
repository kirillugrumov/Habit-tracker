package com.example.habittracker.controller;

import com.example.habittracker.dto.CreateUserRequest;
import com.example.habittracker.dto.UpdateUserRequest;
import com.example.habittracker.dto.UserResponseDto;
import com.example.habittracker.mapper.UserMapper;
import com.example.habittracker.model.User;
import com.example.habittracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDto> dtos = users.stream()
                .map(userMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User savedUser = userService.createUser(user.getUsername(), user.getEmail());
        return new ResponseEntity<>(userMapper.toResponseDto(savedUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User user = userService.getUserById(id);
        userMapper.updateEntity(user, request);

        User updatedUser = userService.updateUser(
                id,
                user.getUsername(),
                user.getEmail()
        );

        return ResponseEntity.ok(userMapper.toResponseDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}