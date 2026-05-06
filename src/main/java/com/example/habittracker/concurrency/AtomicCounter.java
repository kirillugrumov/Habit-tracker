package com.example.habittracker.concurrency;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter implements Counter {

    private final AtomicLong value = new AtomicLong(0L);

    @Override
    public long incrementAndGet() {
        return value.incrementAndGet();
    }

    @Override
    public long get() {
        return value.get();
    }
}
