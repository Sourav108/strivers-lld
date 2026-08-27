# 10 — Distributed Systems Fundamentals

## 🤝 1. Distributed Consensus: Paxos vs Raft

Distributed Consensus algorithms allow a cluster of independent nodes to agree on a sequence of values or state transitions, even when some nodes crash or network messages are delayed.

```mermaid
stateDiagram-v2
    [*] --> Follower
    Follower --> Candidate : Heartbeat Timeout (Election Timer expires)
    Candidate --> Leader : Receives Majority of Votes (> N/2)
    Candidate --> Follower : Discovers higher term Leader
    Leader --> Follower : Discovers higher term server
```

### The Raft Algorithm: 3 Core Sub-Problems
1. **Leader Election**: When the leader fails, followers start randomized election timers (150ms–300ms) to prevent split-vote ties, becoming Candidates and requesting votes. A candidate needs a majority ($\lfloor N/2 \rfloor + 1$) to become Leader.
2. **Log Replication**: The Leader accepts client writes, appends them to its log, and broadcasts `AppendEntries` RPCs. Once a majority of followers acknowledge, the entry is committed.
3. **Safety Guarantee**: If a node's log is behind the majority, it can never be elected leader, ensuring committed entries are never overwritten.

---

## 🔒 2. Distributed Locks & The Fencing Token Pattern

When multiple microservices process shared resources (e.g. inventory or payment capture), a distributed lock prevents concurrent race conditions.

```mermaid
sequenceDiagram
    autonumber
    actor Client1 as Client 1 (Acquires Lock)
    actor Client2 as Client 2
    participant LockService as Lock Service (Redis / Zookeeper)
    participant Storage as Shared Storage

    Client1->>LockService: Acquire Lock (Resource: "order_123")
    LockService-->>Client1: Granted! Fencing Token = 34
    Note over Client1: Long GC Pause / Network Stall (Stalls for 60s)
    Note over LockService: Lock Lease Expires due to timeout
    Client2->>LockService: Acquire Lock (Resource: "order_123")
    LockService-->>Client2: Granted! Fencing Token = 35
    Client2->>Storage: Write Data (with Token = 35)
    Storage-->>Client2: Write Accepted (Max Token seen: 35)
    Note over Client1: Client 1 wakes up from GC pause
    Client1->>Storage: Write Data (with Token = 34)
    Storage-->>Client1: 🚨 REJECTED! (Token 34 < Current Max Token 35)
```

### Key Locking Patterns:
- **Redis `SET key value NX PX milliseconds`**: Simple distributed lock with auto-expiry.
- **Redlock Algorithm**: Multi-node Redis lock acquiring locks on $\ge \lfloor N/2 \rfloor + 1$ independent Redis masters.
- **Fencing Tokens**: A monotonically increasing counter returned with every lock grant. The underlying storage rejects any write containing an older token number, preventing stale write corruption after garbage collection pauses.

---

## 🔑 3. Idempotency Keys in Distributed APIs

An operation is **Idempotent** if applying it multiple times yields the exact same outcome as applying it once ($f(f(x)) = f(x)$).

```mermaid
sequenceDiagram
    autonumber
    actor Client as Mobile Client
    participant Gateway as API Gateway / App Service
    participant Redis as Redis Idempotency Store
    participant DB as Postgres DB

    Client->>Gateway: POST /v1/payments (Idempotency-Key: "uuid-abc-123")
    Gateway->>Redis: SET "idemp:uuid-abc-123" "IN_PROGRESS" NX EX 120
    alt Key already exists
        Gateway-->>Client: Return previously cached response (200 OK)
    else First Time Execution
        Gateway->>DB: Process Transaction & Debit Account
        DB-->>Gateway: Transaction Committed
        Gateway->>Redis: SET "idemp:uuid-abc-123" "{status: 'SUCCESS', txn_id: 'tx_99'}" EX 86400
        Gateway-->>Client: 200 OK (Payment Processed)
    end
```

---

## ⏰ 4. Time, Clocks & Causality: Vector Clocks vs TrueTime

In distributed systems, physical clocks across servers drift by milliseconds due to thermal and network variations (NTP is not synchronized enough for global ordering).

```mermaid
flowchart TD
    TimeOrder["Tracking Event Ordering"] --> NTP["Physical Clocks (NTP)<br/>Clock drift causes data loss on Last-Write-Wins"]
    TimeOrder --> Lamport["Lamport Timestamps<br/>Monotonic counter provides Total Ordering"]
    TimeOrder --> Vector["Vector Clocks (V_A, V_B, V_C)<br/>Tracks causal relationships & detects conflicts"]
    TimeOrder --> TrueTime["Google TrueTime API<br/>Bounded uncertainty interval via GPS + Atomic Clocks"]
```

| Mechanism | Description | Conflict Handling | Best For |
|---|---|---|---|
| **Last-Write-Wins (LWW)** | Overwrites based on server physical timestamp | 🚨 Silent data loss on clock drift | Cassandra non-critical rows |
| **Vector Clocks** | Array of counters per node $[Node_A: 2, Node_B: 1]$ | Detects branching concurrent writes | Dynamo shopping carts, Riak |
| **Google TrueTime** | Guarantees bounded uncertainty $\epsilon \approx 7\text{ms}$ | Wait out the uncertainty window ($2\epsilon$) | Google Spanner global ACID |
