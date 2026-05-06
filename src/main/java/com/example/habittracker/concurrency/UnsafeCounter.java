package com.example.habittracker.concurrency;

public class UnsafeCounter implements Counter {

    private long value = 0L;

    @Override
    public long incrementAndGet() {
        value++;
        return value;
    }

    @Override
    public long get() {
        return value;
    }
}
