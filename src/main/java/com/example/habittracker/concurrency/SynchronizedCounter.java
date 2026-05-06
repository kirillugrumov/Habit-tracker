package com.example.habittracker.concurrency;

public class SynchronizedCounter implements Counter {

    private long value = 0L;

    @Override
    public synchronized long incrementAndGet() {
        value++;
        return value;
    }

    @Override
    public synchronized long get() {
        return value;
    }
}
