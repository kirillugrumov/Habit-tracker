package com.example.habittracker.concurrency;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService service = new RaceConditionDemoService();

    @Test
    void synchronizedAndAtomicCountersReachExpectedValue() {
        List<RaceDemoResult> results = service.runDemo(50, 100_000, 2);

        assertTrue(results.stream().anyMatch(result -> result.mode().equals("synchronized") && result.ok()));
        assertTrue(results.stream().anyMatch(result -> result.mode().equals("atomic") && result.ok()));
    }
}
