package com.example.habittracker.concurrency;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RaceConditionDemoService {

    public List<RaceDemoResult> runDemo(int threads, int tasks, int trials) {
        int safeThreads = Math.max(threads, 50);
        int safeTasks = Math.max(tasks, 100_000);
        int safeTrials = Math.max(trials, 3);

        List<RaceDemoResult> results = new ArrayList<>();
        results.add(runUnsafeBestEffort(safeThreads, safeTasks, safeTrials));
        results.add(runOnce("synchronized", safeThreads, safeTasks, new SynchronizedCounter()));
        results.add(runOnce("atomic", safeThreads, safeTasks, new AtomicCounter()));
        return results;
    }

    private RaceDemoResult runUnsafeBestEffort(int threads, int tasks, int trials) {
        RaceDemoResult bestAttempt = runOnce("unsafe", threads, tasks, new UnsafeCounter());
        long maxLostUpdates = bestAttempt.expected() - bestAttempt.actual();

        for (int i = 1; i < trials; i++) {
            RaceDemoResult attempt = runOnce("unsafe", threads, tasks, new UnsafeCounter());
            long lostUpdates = attempt.expected() - attempt.actual();
            if (lostUpdates > maxLostUpdates) {
                bestAttempt = attempt;
                maxLostUpdates = lostUpdates;
            }
            if (!attempt.ok()) {
                bestAttempt = attempt;
                break;
            }
        }

        return bestAttempt;
    }

    private RaceDemoResult runOnce(String mode, int threads, int tasks, Counter counter) {
        Instant started = Instant.now();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        int perThread = tasks / threads;
        int remainder = tasks % threads;

        for (int t = 0; t < threads; t++) {
            int increments = perThread + (t < remainder ? 1 : 0);
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await(5, TimeUnit.SECONDS);
                    for (int i = 0; i < increments; i++) {
                        counter.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        try {
            ready.await(5, TimeUnit.SECONDS);
            start.countDown();
            done.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            pool.shutdownNow();
        }

        long expected = tasks;
        long actual = counter.get();
        boolean ok = actual == expected;
        long elapsedMs = Duration.between(started, Instant.now()).toMillis();

        return new RaceDemoResult(mode, threads, tasks, expected, actual, ok, elapsedMs);
    }
}
