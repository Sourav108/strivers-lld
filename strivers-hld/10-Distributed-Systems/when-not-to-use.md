# When NOT to Use: Distributed Locks (Redlock / ZooKeeper)

## ❌ When Distributed Locking is the WRONG Choice:

1. **High-Throughput Concurrent Increments (e.g. Likes, Views, Inventory Counters)**:
   - *Why*: Acquiring a distributed lock per click degrades throughput to $< 500 \text{ ops/sec}$ due to network lock acquisition RTT and lock contention timeouts.
   - *Better Choice*: **Atomic Database Increments** (`UPDATE counters SET count = count + 1 WHERE id = ?`), **Redis `INCR`**, or **CRDT PN-Counters**.
2. **Long-Running Batch Workflows**:
   - *Why*: If a process holds a lock for minutes while running batch computations, an unexpected garbage collection pause or network blip causes the lock lease to expire, allowing another worker to acquire the lock and corrupt data.
   - *Better Choice*: **Idempotent task queuing** or **Fencing Tokens**.
