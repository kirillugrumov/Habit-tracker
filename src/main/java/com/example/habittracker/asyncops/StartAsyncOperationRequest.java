package com.example.habittracker.asyncops;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StartAsyncOperationRequest(
        @Min(1) @Max(2_000_000) int iterations,
        @Min(0) @Max(50) int delayMs
) {
}
