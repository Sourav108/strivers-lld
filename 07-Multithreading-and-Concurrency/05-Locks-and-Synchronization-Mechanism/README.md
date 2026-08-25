# 05 - Locks and Synchronization Mechanisms

## Core Idea

Explicit lock utilities in `java.util.concurrent.locks` overcome the limitations of the built-in `synchronized` keyword by offering fine-grained control, timeout capabilities, and non-blocking lock acquisition. **`ReentrantLock`** provides exclusive ownership with `tryLock(timeout)` to prevent indefinite blocking; **`ReentrantReadWriteLock`** maximizes throughput in read-heavy systems by allowing concurrent readers; and **`Semaphore`** manages permit-based access to limit concurrent access to $N$ resources (e.g., connection pools and rate limiters).

---

## 💡 Real-Life Analogies

- **`ReentrantLock` (Key-Protected Fitting Room):** Only one person enters with the exclusive key; other shoppers must wait or give up after a timeout (`tryLock(2, SECONDS)`).
- **`ReadWriteLock` (Public Library Shelf):** 50 people can read books from the same aisle simultaneously (`readLock()`), but when the librarian re-shelves inventory (`writeLock()`), the aisle is closed to all readers and writers.
- **`Semaphore` (Club Token Bucket):** A club gives out 20 entrance tokens. If all 20 tokens are in use, new guests wait at the door until someone exits and returns a token (`release()`).

---

## ⚖️ Concurrency Mechanisms Comparison

| Feature | Monitor (`synchronized`) | `ReentrantLock` | `ReentrantReadWriteLock` | `Semaphore` |
|---|---|---|---|---|
| **Concurrency Limit** | 1 Thread | 1 Thread | Multiple Readers / 1 Writer | **$N$ Threads (Permits)** |
| **Reentrancy** | ✅ Yes | ✅ Yes | ✅ Yes | ❌ No |
| **Timeout Support** | ❌ No (Blocks forever) | ✅ Yes (`tryLock(timeout)`) | ✅ Yes (`tryLock(timeout)`) | ✅ Yes (`tryAcquire(timeout)`) |
| **Ownership Semantics** | ✅ Owning Thread | ✅ Owning Thread | ✅ Owning Thread | ❌ **No Ownership** (Any thread can release) |
| **Fairness Guarantee** | ❌ Unfair | ✅ Configurable (`fair=true`) | ✅ Configurable (`fair=true`) | ✅ Configurable (`fair=true`) |
| **Best Used For** | Simple critical sections | BookMyShow ticket booking with idle timeout | Stock tickers, product catalog caches | DB connection pools, rate limiters, device caps |

---

## ❌ Bad Design (`synchronized` Indefinite Blocking Anti-Pattern)

```java
class BadBookMyShowBooking {
    private int availableSeats = 1;

    // ❌ If User 1 goes idle or network hangs, all other users block forever!
    public synchronized void bookTicket(String user) {
        System.out.println(user + " is booking...");
        // User walks away from screen (simulated 30s pause)
        try { Thread.sleep(30000); } catch (InterruptedException e) {}
        availableSeats--;
    }
}
```

### What is wrong?
- ⚠️ **Indefinite Lock Blocking:** If a thread pauses, stalls, or deadlocks inside `synchronized`, waiting threads hang permanently without any escape hatch.
- ⚠️ **Zero Non-Blocking Checks:** Threads cannot query if the seat is currently free before choosing to wait.
- ⚠️ **Read Bottleneck:** Read-only queries (viewing seat availability) are needlessly blocked while another read is active.

---

## ✅ Good Design (`ReentrantLock`, `ReadWriteLock`, & `Semaphore`)

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

// 1. ReentrantLock with Timed Acquisition (BookMyShow)
class TimedBookingService {
    private int availableSeats = 1;
    private final ReentrantLock lock = new ReentrantLock();

    public boolean bookTicket(String user, long waitTimeMs) {
        boolean lockAcquired = false;
        try {
            // Escape hatch: Wait at most waitTimeMs before abandoning
            lockAcquired = lock.tryLock(waitTimeMs, TimeUnit.MILLISECONDS);
            if (lockAcquired) {
                if (availableSeats > 0) {
                    availableSeats--;
                    return true;
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lockAcquired) lock.unlock(); // Safe owner release
        }
    }
}

// 2. ReadWriteLock for High-Frequency Read Caches (Stock Quotes)
class StockPriceFeed {
    private double price = 100.0;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public double getPrice() {
        rwLock.readLock().lock(); // Shared read lock
        try { return price; }
        finally { rwLock.readLock().unlock(); }
    }

    public void updatePrice(double newPrice) {
        rwLock.writeLock().lock(); // Exclusive write lock
        try { price = newPrice; }
        finally { rwLock.writeLock().unlock(); }
    }
}

// 3. Semaphore for Max Active Device Enforcement (Netflix/TUF+ 2-Device Limit)
class AccountSessionManager {
    private final Semaphore deviceSlots = new Semaphore(2); // 2 concurrent devices

    public boolean loginDevice(String deviceId) {
        return deviceSlots.tryAcquire(); // Non-blocking permit check
    }

    public void logoutDevice(String deviceId) {
        deviceSlots.release(); // Return permit to the pool
    }
}
```

### Why it better demonstrates the concept:
- ✅ **Guaranteed Responsiveness:** `tryLock(timeout)` prevents threads from getting stuck indefinitely.
- ✅ **Concurrent Read Scaling:** `ReadWriteLock` allows infinite simultaneous readers while ensuring write isolation.
- ✅ **Enforced Concurrency Quotas:** `Semaphore` bounds concurrent active sessions to exactly $N$.

---

## Java Classes

- **`TicketBookingTryLock`:** Demonstrates `ReentrantLock.tryLock(timeout, unit)` avoiding indefinite lock waiting.
- **`StockPriceFeed`:** Demonstrates `ReentrantReadWriteLock` with shared read access and exclusive write access.
- **`TUFPlusAccountSession`:** Demonstrates `Semaphore` enforcing a 2-device concurrent login policy.
- **`LocksAndSynchronizationExample` (Main Driver):** Tests and validates all three locking primitives under concurrent multi-threaded workloads.

---

## How It Works

1. **`tryLock(timeout)`:** The thread attempts to acquire the lock. If held by another thread, it sleeps up to the timeout; if not acquired within the window, it returns `false` without blocking.
2. **`ReadWriteLock`:** When a thread acquires `writeLock()`, subsequent `readLock()` requests block until the write completes. When readers hold `readLock()`, writers wait, but additional readers enter instantly.
3. **`Semaphore.tryAcquire()`:** Decrements available permits if $>0$. If permits are 0, it fails immediately (`false`) or blocks until `release()` is called.

---

## When to Use

- **`ReentrantLock`:** Critical sections requiring timeouts (ticket booking, flash sales), fair FIFO queueing, or interruptible lock acquisition.
- **`ReadWriteLock`:** Read-heavy in-memory state (product catalogs, exchange rates, configuration maps) with $>90\%$ read traffic.
- **`Semaphore`:** API Rate limiters, DB connection pools, resource throttlers, multi-device account constraints.

---

## When NOT to Use

- **`Semaphore` for Mutual Exclusion:** While `Semaphore(1)` acts like a mutex, it lacks ownership semantics (any thread can release it). Use `ReentrantLock` instead.
- **`ReadWriteLock` for Write-Heavy Workloads:** If writes are frequent, `ReadWriteLock` introduces extra locking overhead compared to a plain `ReentrantLock`.

---

## LLD Takeaway

Explicit locking constructs (`ReentrantLock`, `ReadWriteLock`, `Semaphore`) are the industry standard for designing **BookMyShow Ticket Booking**, **Stock Market Exchange Feeds**, **DB Connection Pools (HikariCP)**, and **Multi-Tenant Device Limiters** in Low-Level Design.

---

## 🎯 Quick Summary

- **Core Idea:** Explicit lock APIs provide timeouts (`ReentrantLock`), concurrent read scaling (`ReadWriteLock`), and permit quotas (`Semaphore`).
- **Code Demonstrates:** Timed ticket booking with `tryLock`, high-throughput stock feeds with `ReadWriteLock`, and a 2-device login cap with `Semaphore`.
- **LLD Takeaway:** Never use basic `synchronized` when a system requires timeouts, read-heavy optimization, or $N$-resource concurrency throttling.
- **Memorable Rule:** *"Use ReentrantLock for timeouts, ReadWriteLock for read-heavy caches, and Semaphore for token quotas."*
