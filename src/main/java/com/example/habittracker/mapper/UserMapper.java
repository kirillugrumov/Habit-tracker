package com.example.habittracker.mapper;

import com.example.habittracker.dto.CreateUserRequest;
import com.example.habittracker.dto.UpdateUserRequest;
import com.example.habittracker.dto.UserResponseDto;
import com.example.habittracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        return new User(
                request.getUsername(),
                request.getEmail()
        );
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        if (request == null) {
            return;
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
    }
}