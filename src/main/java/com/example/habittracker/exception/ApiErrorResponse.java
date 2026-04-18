package com.example.habittracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Unified API error response")
public record ApiErrorResponse(
        @Schema(description = "Error timestamp", example = "2026-04-18T12:30:15.123")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP status text", example = "Bad Request")
        String error,
        @Schema(description = "General error message", example = "Validation failed")
        String message,
        @Schema(description = "Request path", example = "/api/users")
        String path,
        @Schema(description = "Detailed validation errors")
        List<ApiErrorDetail> details
) {
    public static ApiErrorResponse of(
            int status,
            String error,
            String message,
            String path
    ) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                List.of()
        );
    }

    public static ApiErrorResponse of(
            int status,
            String error,
            String message,
            String path,
            List<ApiErrorDetail> details
    ) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                details
        );
    }
}
