# 04 - Thread Safety and Synchronization

## Core Idea

**Thread Safety** guarantees that a piece of code, data structure, or object maintains consistency and correctness when concurrently accessed by multiple threads without data corruption. When shared mutable state is accessed without coordination, **Race Conditions** occur (where the final outcome depends on thread scheduling timing). Java solves this via three key mechanisms: **`synchronized` monitor locks** (Atomicity + Visibility), the **`volatile` keyword** (Visibility only), and **Atomic Variables** (Lock-free hardware Compare-And-Swap / CAS).

---

## 💡 Real-Life Analogy

### 🎫 TUF+ Flash Sale Counter
- **❌ Unsynchronized Counter:** Two buyers click "Enroll" at the exact same millisecond. Both read current sales = 100, both compute $100 + 1 = 101$, and both write 101 back to the database. Two sales occurred, but the counter only incremented by 1—one purchase vanished due to a race condition.
- **✅ Thread-Safe Counter:** The counter enforces mutual exclusion or atomic CAS updates. Buyer 1 increments $100 \rightarrow 101$, and Buyer 2 immediately increments $101 \rightarrow 102$ without missing updates.

---

## ⚖️ Concurrency Primitives Comparison

| Feature | `volatile` | `synchronized` Block/Method | `AtomicInteger` (CAS) |
|---|---|---|---|
| **Atomicity** | ❌ No (Cannot prevent race on `count++`) | ✅ Yes (Full mutual exclusion) | ✅ Yes (Single-variable atomic operations) |
| **Visibility** | ✅ Yes (Direct main memory read/write) | ✅ Yes (Flushes cache on lock release) | ✅ Yes (Volatile internally) |
| **Locking Mechanism** | 🟢 Lock-free (Zero overhead) | 🔴 Pessimistic Monitor Lock (Blocks threads) | 🟡 Optimistic Non-blocking (CPU CAS loop) |
| **Throughput Under Load** | Maximum (Read-heavy flags) | Moderate (Lock contention overhead) | High (Ideal for counters/metrics) |
| **Best Used For** | Status flags, shutdown triggers, config state. | Critical multi-step compound transactions. | High-frequency counters, sequence generators, metrics. |

---

## 🔬 How Compare-And-Swap (CAS) Works

```
CPU Hardware CAS Loop (Lock-Free Optimistic Concurrency):

     +-----------------------+
     | Read 'expected' (10)  |
     +-----------------------+
                 |
                 v
     +-----------------------+
     | Calculate 'next' (11) |
     +-----------------------+
                 |
                 v
     +-----------------------+
     |  CPU executes CAS:    | ----> Value in memory == expected (10)?
     +-----------------------+       /                             \
                                    / Yes                           \ No (Another thread updated it)
                                   v                                 v
                     [Atomic Swap to 11: SUCCESS!]     [Retry Loop: Read new value & recompute]
```

---

## ❌ Bad Design (Race Condition on Shared Counter)

```java
class UnsafePurchaseCounter {
    private int count = 0;

    // ❌ count++ is NOT atomic! (3 steps: READ -> MODIFY -> WRITE)
    public void increment() {
        count++; 
    }

    public int getCount() { return count; }
}

// 2 threads running 1,000 increments each yield ~1600 instead of 2000!
```

### What is wrong?
- ⚠️ **Non-Atomic Compound Operation:** `count++` compiles down to bytecode instructions: `ILOAD` $\rightarrow$ `IADD` $\rightarrow$ `ISTORE`.
- ⚠️ **Lost Updates:** If Thread A and Thread B read `count = 5` simultaneously, both write back `6`, losing one entire increment.
- ⚠️ **Memory Invisibility:** Without memory barriers, threads cache values in CPU L1/L2 registers, causing stale reads.

---

## ✅ Good Design (Synchronized, Volatile, & Atomic Solutions)

```java
import java.util.concurrent.atomic.AtomicInteger;

// 1. Thread-Safe via Synchronized Block (Pessimistic Locking)
class SynchronizedCounter {
    private final Object lock = new Object();
    private int count = 0;

    public void increment() {
        synchronized (lock) {
            count++;
        }
    }
    public int getCount() { synchronized (lock) { return count; } }
}

// 2. Thread-Safe via AtomicInteger (Lock-Free CAS)
class AtomicCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // Atomic hardware CAS instruction
    }
    public int getCount() { return count.get(); }
}

// 3. Thread-Safe Visibility via Volatile (Flag state)
class ShutdownSignal {
    private volatile boolean running = true;

    public void stop() { running = false; }
    public boolean isRunning() { return running; }
}
```

### Why it better demonstrates the concept:
- ✅ **Guaranteed Invariants:** `SynchronizedCounter` and `AtomicCounter` guarantee exact final totals under high concurrent thread load.
- ✅ **Lock-Free Scalability:** `AtomicInteger` eliminates thread suspension and context-switching overhead using CPU-level CAS instructions.
- ✅ **Immediate Visibility:** `volatile` ensures CPU cache coherency across multi-core processors.

---

## Java Classes

- **`UnsafePurchaseCounter`:** Demonstrates race condition anomalies on uncoordinated shared state.
- **`SynchronizedPurchaseCounter`:** Demonstrates mutual exclusion using fine-grained `synchronized` lock blocks.
- **`AtomicPurchaseCounter`:** Demonstrates lock-free high-throughput concurrency via `AtomicInteger.incrementAndGet()`.
- **`VolatileFlagController`:** Demonstrates `volatile` ensuring instant cross-thread cache visibility for shutdown triggers.
- **`ThreadSafetyAndSynchronizationExample` (Main Driver):** Benchmarks all 4 approaches across 20 concurrent worker threads.

---

## How It Works

1. **Monitor Lock (`synchronized`):** Thread acquires the object's monitor. Other threads trying to enter are transitioned to `BLOCKED`. Upon exit, the monitor is released and changes are flushed to Main Memory.
2. **Volatile Memory Barrier:** CPU inserts memory fence instructions forcing all reads and writes directly to RAM, preventing compiler instruction reordering.
3. **Atomic CAS Loop:** Uses `Unsafe.compareAndSwapInt` to update memory only if the current value matches expectations, retrying in a tight CPU loop if contention occurs.

---

## When to Use

- **`synchronized`:** Multi-variable state invariants (e.g. `withdraw(acc1, acc2, amount)`), compound business transactions.
- **`AtomicInteger` / `AtomicReference`:** Counters, metrics, rate limiter buckets, sequence ID generators.
- **`volatile`:** Boolean flags (e.g. `volatile boolean isShutdown`), single-producer status updates.

---

## When NOT to Use

- **`volatile` for Compound Updates:** Never use `volatile int count` for `count++` (it does NOT prevent race conditions).
- **Global Giant `synchronized` Methods:** Placing `synchronized` on entire classes turns parallel multi-core machines into slow single-threaded bottlenecks. Lock only critical sections.

---

## LLD Takeaway

Thread safety is mandatory when designing **In-Memory Caches (LRU Cache)**, **Connection Pools**, **Rate Limiters (Token Bucket)**, and **Order Inventory Engines** in Low-Level Design interviews.

---

## 🎯 Quick Summary

- **Core Idea:** Thread safety prevents race conditions on shared mutable state using locks (`synchronized`), memory barriers (`volatile`), or lock-free CAS (`AtomicInteger`).
- **Code Demonstrates:** Benchmarking an unsafe counter (lost updates) against `synchronized` and `AtomicInteger` (100% correct updates).
- **LLD Takeaway:** Choose `volatile` for visibility flags, `Atomic*` for high-frequency counters, and `synchronized` for multi-step compound transactions.
- **Memorable Rule:** *"Volatile provides visibility; Synchronized provides atomicity; Atomic classes provide lock-free atomicity via CAS."*
