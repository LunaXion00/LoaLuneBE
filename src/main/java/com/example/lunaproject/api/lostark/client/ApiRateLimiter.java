package com.example.lunaproject.api.lostark.client;

import java.util.concurrent.atomic.AtomicInteger;

public class ApiRateLimiter {
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static volatile long lastResetTime = System.currentTimeMillis();

    public static synchronized void checkAndWait() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastResetTime;

        if (elapsedTime >= 60_000) {
            resetCounter();
        }

        if (requestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            long waitTime = 60_000 - elapsedTime;
            System.out.println("API 요청 제한 초과! " + waitTime / 1000 + "초 대기...");
            Thread.sleep(waitTime);
            resetCounter();
        }
    }

    public static synchronized void resetCounter() {
        requestCount.set(0);
        lastResetTime = System.currentTimeMillis();
    }
}
