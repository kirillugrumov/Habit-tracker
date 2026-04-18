package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User response")
public class UserResponseDto {
    @Schema(description = "User id", example = "1")
    private Long id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "Email", example = "john@example.com")
    private String email;
}
