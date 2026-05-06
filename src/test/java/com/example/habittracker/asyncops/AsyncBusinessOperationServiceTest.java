package com.example.habittracker.asyncops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        AsyncBusinessOperationService.class,
        AsyncBusinessOperationExecutor.class,
        AsyncTaskRegistry.class
})
@Import(com.example.habittracker.config.AsyncConfig.class)
class AsyncBusinessOperationServiceTest {

    @Autowired
    private AsyncBusinessOperationService service;

    @Test
    void startReturnsTaskIdAndCompletesInBackground() throws Exception {
        StartAsyncOperationResponse response = service.start(new StartAsyncOperationRequest(50_000, 1));
        assertNotNull(response.taskId());
        assertTrue(response.taskId().matches("\\d+"));

        AsyncTaskSnapshot initial = service.status(response.taskId());
        assertNotNull(initial.status());

        long deadline = System.currentTimeMillis() + 5_000;
        AsyncTaskSnapshot snapshot = initial;
        while (System.currentTimeMillis() < deadline) {
            snapshot = service.status(response.taskId());
            if (snapshot.status() == AsyncTaskStatus.SUCCESS || snapshot.status() == AsyncTaskStatus.FAILED) {
                break;
            }
            Thread.sleep(50);
        }

        assertEquals(AsyncTaskStatus.SUCCESS, snapshot.status());
        assertNotNull(snapshot.finishedAt());
        assertTrue(snapshot.result() >= 0);
    }
}
