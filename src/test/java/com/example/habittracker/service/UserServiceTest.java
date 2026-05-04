package com.example.habittracker.service;

import com.example.habittracker.cache.HabitSearchCache;
import com.example.habittracker.dto.CreateUserRequest;
import com.example.habittracker.dto.UpdateUserRequest;
import com.example.habittracker.dto.UserResponseDto;
import com.example.habittracker.mapper.UserMapper;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HabitSearchCache habitSearchCache;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveAndInvalidateCache() {
        CreateUserRequest request = new CreateUserRequest("john", "john@mail.com");
        User user = createUser(null, "john", "john@mail.com");
        User savedUser = createUser(1L, "john", "john@mail.com");
        UserResponseDto dto = new UserResponseDto(1L, "john", "john@mail.com");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@mail.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(dto);

        UserResponseDto result = userService.createUser(request);

        assertEquals(dto, result);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void createUser_shouldThrowWhenUsernameExists() {
        CreateUserRequest request = new CreateUserRequest("john", "john@mail.com");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("User with username 'john' already exists", exception.getMessage());
    }

    @Test
    void createUser_shouldThrowWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest("john", "john@mail.com");
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@mail.com")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("User with email 'john@mail.com' already exists", exception.getMessage());
    }

    @Test
    void getAllUsers_shouldReturnMappedDtos() {
        User first = createUser(1L, "john", "john@mail.com");
        User second = createUser(2L, "anna", "anna@mail.com");
        UserResponseDto firstDto = new UserResponseDto(1L, "john", "john@mail.com");
        UserResponseDto secondDto = new UserResponseDto(2L, "anna", "anna@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(first, second));
        when(userMapper.toResponseDto(first)).thenReturn(firstDto);
        when(userMapper.toResponseDto(second)).thenReturn(secondDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getUserById_shouldReturnMappedDto() {
        User user = createUser(1L, "john", "john@mail.com");
        UserResponseDto dto = new UserResponseDto(1L, "john", "john@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(dto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getUserById_shouldThrowWhenMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateUsernameAndEmailAndInvalidateCache() {
        User user = createUser(1L, "john", "john@mail.com");
        UpdateUserRequest request = new UpdateUserRequest("johnny", "johnny@mail.com");
        UserResponseDto dto = new UserResponseDto(1L, "johnny", "johnny@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("johnny")).thenReturn(false);
        when(userRepository.existsByEmail("johnny@mail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(dto);

        UserResponseDto result = userService.updateUser(1L, request);

        assertEquals(dto, result);
        assertEquals("johnny", user.getUsername());
        assertEquals("johnny@mail.com", user.getEmail());
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void updateUser_shouldThrowWhenUsernameTaken() {
        User user = createUser(1L, "john", "john@mail.com");
        UpdateUserRequest request = new UpdateUserRequest("johnny", "johnny@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("johnny")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals("Name 'johnny' is already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowWhenEmailTaken() {
        User user = createUser(1L, "john", "john@mail.com");
        UpdateUserRequest request = new UpdateUserRequest("john", "johnny@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("johnny@mail.com")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals("Email 'johnny@mail.com' is already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteAndInvalidateCache() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(habitSearchCache).invalidateAll();
    }

    @Test
    void deleteUser_shouldThrowWhenMissing() {
        when(userRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
