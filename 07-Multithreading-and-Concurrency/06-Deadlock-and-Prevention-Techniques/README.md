# 06 - Deadlock and Prevention Techniques

## Core Idea

A **Deadlock** is a catastrophic concurrency state where two or more threads are permanently blocked, each holding a resource while waiting for another resource held by the other, causing execution to freeze indefinitely. Deadlocks can only occur if all **Four Coffman Conditions** (Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait) hold simultaneously. Preventing deadlocks involves breaking at least one of these four conditions using techniques like **Global Lock Ordering** or **`tryLock()` with Timeout Backoff**.

---

## 💡 Real-Life Analogies

### 🚂 4 Trains at a 4-Way Crossing
Imagine four trains arriving at a 4-way intersection simultaneously:
- Train A blocks Track B, Train B blocks Track C, Train C blocks Track D, and Train D blocks Track A.
- Each train holds one track and waits for the next to clear, forming a circular dependency where no train can move.

### 🍝 The Dining Philosophers Problem
Five philosophers sit around a round table with 5 single forks between them:
- Each philosopher needs **both left and right forks** to eat.
- If all 5 philosophers pick up their left fork simultaneously, every philosopher holds 1 fork and waits forever for the right fork held by their neighbor—a classic circular deadlock.

---

## 🔒 The 4 Coffman Conditions

```
+-----------------------------------------------------------------------------+
| COFFMAN CONDITIONS (All 4 Required for Deadlock)                            |
|                                                                             |
| 1. Mutual Exclusion  : Only 1 thread can hold the resource at a time.       |
| 2. Hold and Wait     : Thread holds Resource A while waiting for Resource B.|
| 3. No Preemption     : Locks cannot be forcibly revoked from holding threads|
| 4. Circular Wait     : T1 waits for T2 -> T2 waits for T3 -> T3 waits for T1|
+-----------------------------------------------------------------------------+
```

> [!IMPORTANT]
> To prevent deadlocks, software architects must break **at least ONE** of these four conditions.

---

## ❌ Bad Design (Deadlock via Unordered Bank Transfers)

```java
class BankAccount {
    private final String id;
    private int balance;
    public BankAccount(String id, int balance) { this.id = id; this.balance = balance; }
}

// ❌ Transferring without global lock ordering
void transfer(BankAccount from, BankAccount to, int amount) {
    synchronized (from) { // Thread 1 locks Account A
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        synchronized (to) {   // Thread 1 waits for Account B (held by Thread 2!)
            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}

// Thread 1: transfer(AccountA, AccountB)  --> Locks A, wants B
// Thread 2: transfer(AccountB, AccountA)  --> Locks B, wants A
// 💥 SYSTEM PERMANENTLY FREEZES IN DEADLOCK!
```

### What is wrong?
- ⚠️ **Circular Wait:** Thread 1 holds Lock A and waits for Lock B; Thread 2 holds Lock B and waits for Lock A.
- ⚠️ **No Timeout / Escape Hatch:** Threads block forever inside `synchronized` without timing out.

---

## ✅ Good Design (Deadlock Prevention Strategies)

### Strategy 1: Global Lock Ordering (Breaks Circular Wait)
Sort resources by unique ID before acquiring locks:

```java
void transferWithLockOrdering(BankAccount from, BankAccount to, int amount) {
    BankAccount firstLock = from.getId().compareTo(to.getId()) < 0 ? from : to;
    BankAccount secondLock = from.getId().compareTo(to.getId()) < 0 ? to : from;

    // Both threads always acquire locks in identical alphabetical/numerical order!
    synchronized (firstLock) {
        synchronized (secondLock) {
            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}
```

### Strategy 2: `tryLock()` with Timeout & Backoff (Breaks Hold and Wait)
Attempt to acquire locks with timeouts; if unavailable, release acquired locks and retry:

```java
boolean transferWithTryLock(BankAccount from, BankAccount to, int amount) throws InterruptedException {
    while (true) {
        if (from.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
            try {
                if (to.getLock().tryLock(100, TimeUnit.MILLISECONDS)) {
                    try {
                        from.withdraw(amount);
                        to.deposit(amount);
                        return true; // Transfer succeeded!
                    } finally {
                        to.getLock().unlock();
                    }
                }
            } finally {
                from.getLock().unlock(); // Release lock on failure to avoid holding
            }
        }
        // Backoff randomly before retrying to prevent livelock
        Thread.sleep((long) (Math.random() * 50));
    }
}
```

---

## 🗄️ Database Deadlock Schemes (Wait-Die vs. Wound-Wait)

| Scheme | Timestamp / Priority Rule | Preemptive? | Action on Lock Contention |
|---|---|---|---|
| **Wait-Die** | Older transaction requests lock held by Younger: | ❌ Non-Preemptive | **Older WAITS**, **Younger DIES** (Aborted & restarted). |
| **Wound-Wait** | Older transaction requests lock held by Younger: | ✅ Preemptive | **Older WOUNDS (Preempts/Aborts) Younger**, **Younger WAITS**. |

---

## Java Classes

- **`DeadlockProneBankTransfer`:** Demonstrates a real-world deadlock between bidirectional account transfers.
- **`OrderedLockBankTransfer`:** Demonstrates deadlock prevention by sorting account IDs globally before acquiring locks.
- **`TimedBackoffBankTransfer`:** Demonstrates deadlock prevention using `ReentrantLock.tryLock()` with exponential/random backoff.
- **`DeadlockPreventionExample` (Main Driver):** Executes safe concurrent multi-threaded transfers without freezing.

---

## How It Works

1. **Global Lock Ordering:** Regardless of whether Transfer 1 is $A \rightarrow B$ or Transfer 2 is $B \rightarrow A$, both threads sort locks and acquire $A$ first, then $B$. Circular wait cannot form.
2. **`tryLock()` Backoff:** If a thread cannot acquire the second lock within the timeout window, it releases the first lock and sleeps, allowing competing threads to finish.

---

## When to Use

- **Financial Ledger & Banking Systems:** Multi-account money transfers, double-entry bookkeeping transactions.
- **Distributed Resource Allocation:** Dining philosophers problems, GPU/resource managers allocating multiple hardware devices.
- **Multi-Table Relational Database Updates:** Enforcing alphabetical table lock acquisition in batch SQL scripts.

---

## When NOT to Use

- **Single Resource Operations:** If operations only need 1 lock, deadlocks are mathematically impossible.
- **Overly Complex Backoff Logic:** When lock ordering is possible, prefer **Lock Ordering** over retry loops (which risk **Livelock**).

---

## LLD Takeaway

Deadlock avoidance via **Global Lock Ordering** and **`tryLock()` Timeouts** is a core design requirement for **Distributed Transactions**, **Banking Transfer Engines**, and **Resource Allocators** in Low-Level Design interviews.

---

## 🎯 Quick Summary

- **Core Idea:** Deadlocks occur when circular wait conditions freeze threads; breaking at least one Coffman condition guarantees deadlock freedom.
- **Code Demonstrates:** Solving bidirectional bank account transfer deadlocks using deterministic resource ID ordering and `tryLock()` backoff.
- **LLD Takeaway:** Always acquire multiple locks in a consistent global sequence (e.g. sorting by unique ID) to eliminate circular dependencies.
- **Memorable Rule:** *"Acquire locks in global order to break the circle; use tryLock with timeouts to prevent holding forever."*
