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
@Schema(description = "Request body for updating a user")
public class UpdateUserRequest {
    @Schema(description = "Updated username", example = "john_updated")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Updated email", example = "john.updated@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
