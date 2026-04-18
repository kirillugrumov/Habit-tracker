package com.example.habittracker.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed validation or request error item")
public record ApiErrorDetail(
        @Schema(description = "Field or property name", example = "email")
        String field,
        @Schema(description = "Validation or error message", example = "Email should be valid")
        String message,
        @Schema(description = "Rejected value", example = "invalid-email")
        Object rejectedValue
) {
}
