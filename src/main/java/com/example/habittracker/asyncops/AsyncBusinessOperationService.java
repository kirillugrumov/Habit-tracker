package com.example.habittracker.asyncops;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncBusinessOperationService {

    private final AsyncTaskRegistry registry;

    public AsyncBusinessOperationService(AsyncTaskRegistry registry) {
        this.registry = registry;
    }

    public StartAsyncOperationResponse start(StartAsyncOperationRequest request) {
        String taskId = UUID.randomUUID().toString();
        registry.create(taskId);
        executeAsync(taskId, request);
        return new StartAsyncOperationResponse(taskId);
    }

    public AsyncTaskSnapshot status(String taskId) {
        return registry.getRequired(taskId);
    }

    @Async("businessExecutor")
    public CompletableFuture<Void> executeAsync(String taskId, StartAsyncOperationRequest request) {
        registry.markRunning(taskId);
        try {
            long sum = 0L;
            int iterations = request.iterations();
            int delayMs = request.delayMs();
            for (int i = 0; i < iterations; i++) {
                sum += (i % 10);
                if (delayMs > 0 && i % 10_000 == 0) {
                    Thread.sleep(delayMs);
                }
            }
            registry.markSuccess(taskId, sum);
            return CompletableFuture.completedFuture(null);
        } catch (Throwable ex) {
            registry.markFailed(taskId, ex);
            return CompletableFuture.failedFuture(ex);
        }
    }
}
