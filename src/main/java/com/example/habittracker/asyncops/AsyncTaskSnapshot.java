package com.example.habittracker.asyncops;

import java.time.Instant;

public record AsyncTaskSnapshot(
        String taskId,
        AsyncTaskStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Long result,
        String error
) {
}
