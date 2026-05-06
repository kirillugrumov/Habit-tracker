package com.example.habittracker.asyncops;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncBusinessOperationExecutor {

    private final AsyncTaskRegistry registry;

    public AsyncBusinessOperationExecutor(AsyncTaskRegistry registry) {
        this.registry = registry;
    }

    @Async("businessExecutor")
    public CompletableFuture<Void> executeAsync(String taskId, StartAsyncOperationRequest request) {
        registry.markRunning(taskId);
        try {
            long sum = 0L;
            int iterations = request.iterations();
            for (int i = 0; i < iterations; i++) {
                sum += (i % 10);
            }
            int delaySeconds = request.delaySeconds();
            if (delaySeconds > 0) {
                Thread.sleep(delaySeconds * 1_000L);
            }
            registry.markSuccess(taskId, sum);
            return CompletableFuture.completedFuture(null);
        } catch (Throwable ex) {
            registry.markFailed(taskId, ex);
            return CompletableFuture.failedFuture(ex);
        }
    }
}
