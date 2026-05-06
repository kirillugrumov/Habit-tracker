package com.example.habittracker.asyncops;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AsyncTaskRegistry {

    private final Map<String, AsyncTaskSnapshot> tasks = new ConcurrentHashMap<>();

    public AsyncTaskSnapshot create(String taskId) {
        AsyncTaskSnapshot snapshot = new AsyncTaskSnapshot(
                taskId,
                AsyncTaskStatus.NEW,
                Instant.now(),
                null,
                null,
                null,
                null
        );
        tasks.put(taskId, snapshot);
        return snapshot;
    }

    public AsyncTaskSnapshot getRequired(String taskId) {
        AsyncTaskSnapshot snapshot = tasks.get(taskId);
        if (snapshot == null) {
            throw new EntityNotFoundException("Task not found with id: " + taskId);
        }
        return snapshot;
    }

    public void markRunning(String taskId) {
        tasks.compute(taskId, (id, prev) -> {
            AsyncTaskSnapshot base = requirePrev(id, prev);
            return new AsyncTaskSnapshot(
                    base.taskId(),
                    AsyncTaskStatus.RUNNING,
                    base.createdAt(),
                    Instant.now(),
                    null,
                    null,
                    null
            );
        });
    }

    public void markSuccess(String taskId, long result) {
        tasks.compute(taskId, (id, prev) -> {
            AsyncTaskSnapshot base = requirePrev(id, prev);
            return new AsyncTaskSnapshot(
                    base.taskId(),
                    AsyncTaskStatus.SUCCESS,
                    base.createdAt(),
                    base.startedAt(),
                    Instant.now(),
                    result,
                    null
            );
        });
    }

    public void markFailed(String taskId, Throwable error) {
        tasks.compute(taskId, (id, prev) -> {
            AsyncTaskSnapshot base = requirePrev(id, prev);
            return new AsyncTaskSnapshot(
                    base.taskId(),
                    AsyncTaskStatus.FAILED,
                    base.createdAt(),
                    base.startedAt(),
                    Instant.now(),
                    null,
                    (error == null ? "Unknown error" : error.getClass().getSimpleName() + ": " + error.getMessage())
            );
        });
    }

    private static AsyncTaskSnapshot requirePrev(String id, AsyncTaskSnapshot prev) {
        if (prev == null) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        return prev;
    }
}
