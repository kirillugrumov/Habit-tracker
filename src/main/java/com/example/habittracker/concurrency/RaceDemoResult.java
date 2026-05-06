package com.example.habittracker.concurrency;

public record RaceDemoResult(
        String mode,
        int threads,
        int tasks,
        long expected,
        long actual,
        boolean ok,
        long elapsedMs
) {
}
