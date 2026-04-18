package com.example.habittracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Base request DTO with a name field")
public abstract class BaseRequestDto {
    @Schema(description = "Entity name", example = "Morning Run")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
}
