# 07 - Producer-Consumer Problem (Bounded Buffer)

## Core Idea

The **Producer-Consumer Problem** (or Bounded Buffer Problem) is a classic multi-threaded synchronization pattern where one or more **Producer threads** generate data and push it into a shared finite buffer, while one or more **Consumer threads** extract and process that data. The core challenge is coordinating threads so producers wait when the buffer is full, consumers wait when the buffer is empty, and mutual exclusion is preserved without busy-waiting or race conditions using `wait()` and `notifyAll()`.

---

## 💡 Real-Life Analogies

### ☕ The Barista Coffee Machine
- **Barista (Producer):** Brews fresh coffee and places cups onto a service counter.
- **Service Counter (Bounded Buffer):** Can only hold 5 coffee cups at a time. If 5 cups are waiting, the barista must pause brewing (`wait()`).
- **Customer (Consumer):** Grabs a cup to drink. If the counter is empty, the customer must wait (`wait()`). When a customer takes a cup, they signal the barista to brew more (`notifyAll()`).

---

## 🏗️ Architecture & Bounded Buffer Queue

```
   [User Thread 1] ----\
   [User Thread 2] ------> [submit()] ---> +---------------------------------------+ ---> [poll()] ---> [Judge Worker 1]
   [User Thread 3] ----/                   | BOUNDED QUEUE (Capacity = 5)          |               ---> [Judge Worker 2]
    (PRODUCERS)                            | [Sub-1] [Sub-2] [Sub-3] [Sub-4] [Sub-5|                 (CONSUMERS)
                                           +---------------------------------------+
                                              ^                                 ^
                                              | Full: Producers wait()          | Empty: Consumers wait()
                                              \---------------------------------/
                                                    Coordinated via notifyAll()
```

---

## ❌ Bad Design (Busy-Waiting Spinlock Anti-Pattern)

```java
class BadSubmissionQueue {
    private final Queue<String> queue = new LinkedList<>();

    // ❌ Burning 100% CPU in busy-wait polling loops!
    public void submit(String task) {
        while (queue.size() >= 5) {
            // Busy spinning: wastes millions of CPU cycles every second!
        }
        queue.offer(task);
    }

    public String consume() {
        while (queue.isEmpty()) {
            // Busy spinning!
        }
        return queue.poll();
    }
}
```

### What is wrong?
- ⚠️ **CPU Thrashing (Busy Waiting):** Threads spin in empty `while` loops, pegging CPU cores at 100% load.
- ⚠️ **Race Conditions:** Lack of `synchronized` monitor locks causes simultaneous `offer()` and `poll()` calls to corrupt linked list node pointers.
- ⚠️ **No Signaling:** Consumers have no way of knowing when a producer has inserted an item without continuous polling.

---

## ✅ Good Design (`wait()`, `notifyAll()`, and `while` Loop Guard)

```java
import java.util.LinkedList;
import java.util.Queue;

class SubmissionQueue {
    private final Queue<String> queue = new LinkedList<>();
    private final int MAX_CAPACITY = 5;

    // 1. Producer Method
    public synchronized void submit(String submission) throws InterruptedException {
        // 🔒 Always check condition in a while loop to guard against spurious wakeups!
        while (queue.size() == MAX_CAPACITY) {
            System.out.println("⏳ Queue full (" + MAX_CAPACITY + "). Producer waiting...");
            wait(); // Releases monitor lock and suspends thread
        }

        queue.offer(submission);
        System.out.println("📥 Submitted: " + submission + " (Queue size: " + queue.size() + ")");

        // Wake up waiting consumers
        notifyAll();
    }

    // 2. Consumer Method
    public synchronized String consume(String judgeName) throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("⏳ " + judgeName + " waiting for submissions...");
            wait(); // Releases monitor lock and suspends thread
        }

        String task = queue.poll();
        System.out.println("⚙️ " + judgeName + " evaluating: " + task);

        // Wake up waiting producers
        notifyAll();
        return task;
    }
}
```

### Why it better demonstrates the concept:
- ✅ **Zero CPU Waste:** Threads enter the `WAITING` state and consume 0 CPU cycles until signaled.
- ✅ **Spurious Wakeup Defense:** Using `while(condition)` ensures that when a thread wakes up, it re-verifies buffer bounds before proceeding.
- ✅ **Thread Safety & Mutual Exclusion:** `synchronized` ensures only one thread modifies the internal queue structure at any instant.

---

## 🔑 `notify()` vs. `notifyAll()`

| Feature | `notify()` | `notifyAll()` (Recommended) |
|---|---|---|
| **Waking Mechanism** | Wakes **1 random thread** waiting on the monitor. | Wakes **all threads** waiting on the monitor. |
| **Risk of Lost Signals** | ⚠️ High! If a producer wakes another producer instead of a consumer, both can get stuck waiting (Deadlock). | 🟢 Zero risk. All waiting threads wake and re-check their condition. |
| **Performance** | Slightly faster (1 context switch). | Negligible overhead in typical queues; guarantees correctness. |

---

## Java Classes

- **`Submission`:** Domain entity representing a code submission with auto-incrementing ID.
- **`SubmissionQueue`:** Thread-safe bounded buffer coordinating producers and consumers via `wait()` and `notifyAll()`.
- **`ProducerConsumerExample` (Main Driver):** Spawns 3 fast user producer threads and 2 slower judge consumer threads to demonstrate queue buffering under load.

---

## How It Works

1. Users produce submissions faster than judges can evaluate them.
2. The buffer absorbs the burst until it reaches `MAX_CAPACITY = 5`.
3. Subsequent producer threads enter `wait()`, pausing execution.
4. When a judge finishes evaluating a submission, it calls `poll()` and triggers `notifyAll()`.
5. Paused producers wake up, re-verify `queue.size() < MAX_CAPACITY`, and successfully enqueue their tasks.

---

## When to Use

- **Asynchronous Task Queues:** Web request pipelines, message brokers (Kafka, RabbitMQ channels), logging pipelines (Log4j AsyncAppender).
- **Online Judges & Batch Processing Engines:** TUF+ code evaluation, video transcoding queues, email delivery workers.
- **Thread Pool Work Queues:** Internal implementations of `ArrayBlockingQueue` and `LinkedBlockingQueue`.

---

## When NOT to Use Raw `wait()`/`notify()`

- **Production Microservices:** In production, use standard high-level concurrent collections from `java.util.concurrent` (e.g., **`BlockingQueue`**, **`ArrayBlockingQueue`**, or **`LinkedBlockingQueue`**) rather than implementing low-level `wait()`/`notifyAll()` manually.

---

## LLD Takeaway

The Producer-Consumer Pattern is the architectural cornerstone for **Message Queues**, **Async Logging Pipelines**, **Rate Limiters**, and **Worker Pools** in Low-Level Design interviews. Always demonstrate understanding of bounded capacity, `while` loop checks for spurious wakeups, and `notifyAll()` signaling.

---

## 🎯 Quick Summary

- **Core Idea:** Coordinate producers and consumers over a shared bounded buffer using mutual exclusion (`synchronized`) and conditional signaling (`wait()`/`notifyAll()`).
- **Code Demonstrates:** A bounded `SubmissionQueue` handling bursty user code submissions and slower backend judge evaluations without busy-waiting.
- **LLD Takeaway:** Always wrap `wait()` inside a `while` loop to guard against spurious wakeups, and prefer `notifyAll()` over `notify()` to prevent lost signals.
- **Memorable Rule:** *"Wait in a while loop; signal with notifyAll when the buffer state changes."*
