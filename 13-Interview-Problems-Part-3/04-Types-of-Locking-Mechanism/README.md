# Types of Locking Mechanisms in Low-Level Design

In financial, ride-booking, inventory, and concurrent distributed systems, managing concurrent access to shared mutable resources is a core requirement.

---

## 1. Overview of Locking Strategies

| Mechanism | Level | Pros | Cons | Best Used For |
|---|---|---|---|---|
| **Database Row-Level Locking (`SELECT FOR UPDATE`)** | Database Engine | Guaranteed ACID compliance; prevents phantom reads. | Locks held during entire DB transaction; reduced throughput. | Banking ledgers, order checkouts. |
| **Optimistic Locking (`@Version` / Timestamp)** | Application / ORM | High read throughput; zero lock contention overhead. | Retry storms under heavy write contention. | High-read e-commerce catalogs, user profile edits. |
| **Pessimistic In-Memory Locking (`synchronized` / `ReentrantLock`)** | JVM Process | Microsecond execution speed; zero network overhead. | Works only on a single JVM instance; vulnerable to deadlocks without sorted locking. | In-memory caches, localized queues, single-instance LLD interviews. |
| **Distributed Locking (Redis Redlock / ZooKeeper)** | Distributed System | Works across multiple microservice pods; TTL prevents deadlocks. | Network latency overhead; clock drift sensitivity in Redlock. | Distributed ride matching, payment processing, distributed wallets. |

---

## 2. Approach 1: Database Row-Level Locking (Pessimistic)

```sql
BEGIN TRANSACTION;
-- Locks rows with ID 1 and ID 2 in order
SELECT balance FROM wallet WHERE id = 1 FOR UPDATE;
SELECT balance FROM wallet WHERE id = 2 FOR UPDATE;

-- Validate balance and execute atomic updates
UPDATE wallet SET balance = balance - 100 WHERE id = 1;
UPDATE wallet SET balance = balance + 100 WHERE id = 2;

INSERT INTO transaction (from_id, to_id, amount, status) VALUES (1, 2, 100, 'COMPLETED');
COMMIT;
```

---

## 3. Approach 2: Optimistic Locking with Version Field

```java
public class Wallet {
    private int id;
    private long balance;
    private int version; // Incremented on each update
}

// Update Query:
// UPDATE wallet SET balance = balance - 100, version = version + 1 
// WHERE id = 1 AND version = currentVersion;
// If rows affected == 0 -> Conflict detected -> Retry or abort
```

---

## 4. Approach 3: Deterministic Ordered Application Locking (Deadlock Prevention)

When transferring between two accounts simultaneously ($A \rightarrow B$ and $B \rightarrow A$):

```java
// Always acquire locks in deterministic order sorted by ID
Wallet firstLock  = fromWallet.getId() < toWallet.getId() ? fromWallet : toWallet;
Wallet secondLock = fromWallet.getId() < toWallet.getId() ? toWallet : fromWallet;

synchronized (firstLock) {
    synchronized (secondLock) {
        fromWallet.debit(amount);
        toWallet.credit(amount);
    }
}
```

---

## 5. Summary & Decision Matrix

- **Single JVM / Interview Coding:** Use **Deterministic Ordered Locking** with `synchronized` or `ReentrantLock`.
- **Relational DB / High Data Integrity:** Use **Pessimistic Row-Level Locking** (`SELECT FOR UPDATE`).
- **Distributed Microservices:** Use **Distributed Locks** via Redis (`SET lock_key uuid NX PX 200`).
