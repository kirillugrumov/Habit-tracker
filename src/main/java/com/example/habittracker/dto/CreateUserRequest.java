package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a user")
public class CreateUserRequest {
    @Schema(description = "Username", example = "john_doe")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "User email", example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
