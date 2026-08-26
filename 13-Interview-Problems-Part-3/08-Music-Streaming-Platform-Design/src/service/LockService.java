package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockService {
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public boolean acquire(String key, long timeoutMs) {
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            return lock.tryLock(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void release(String key) {
        ReentrantLock lock = locks.get(key);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
