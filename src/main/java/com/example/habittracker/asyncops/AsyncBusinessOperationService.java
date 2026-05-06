package com.example.habittracker.asyncops;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AsyncBusinessOperationService {

    private final AtomicLong taskSequence = new AtomicLong(0L);
    private final AsyncTaskRegistry registry;
    private final AsyncBusinessOperationExecutor executor;

    public AsyncBusinessOperationService(AsyncTaskRegistry registry, AsyncBusinessOperationExecutor executor) {
        this.registry = registry;
        this.executor = executor;
    }

    public StartAsyncOperationResponse start(StartAsyncOperationRequest request) {
        String taskId = String.valueOf(taskSequence.incrementAndGet());
        registry.create(taskId);
        executor.executeAsync(taskId, request);
        return new StartAsyncOperationResponse(taskId);
    }

    public AsyncTaskSnapshot status(String taskId) {
        return registry.getRequired(taskId);
    }
}
