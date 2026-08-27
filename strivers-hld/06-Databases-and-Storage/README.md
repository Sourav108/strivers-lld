# 06 — Databases & Storage

## 🗄️ 1. SQL vs NoSQL: The Complete Decision Matrix

```mermaid
flowchart TD
    DB["Database Paradigms"]
    
    subgraph Structured["Structured & Analytical"]
        SQL["Relational (SQL)<br/>PostgreSQL / MySQL"]
        Col["Wide-Column / OLAP<br/>Cassandra / ClickHouse"]
    end

    subgraph SemiStructured["Flexible & Specialized"]
        KV["Key-Value<br/>Redis / DynamoDB"]
        Doc["Document<br/>MongoDB / Couchbase"]
        Graph["Graph DB<br/>Neo4j / Neptune"]
    end

    DB --> Structured
    DB --> SemiStructured
```

| Dimension | Relational (SQL) | Document / Key-Value (NoSQL) | Wide-Column (NoSQL) |
|---|---|---|---|
| **Schema** | Rigid, predefined tabular schema | Dynamic, flexible JSON/schema-less | Wide-column families with row keys |
| **Transactions** | Strict **ACID** (Multi-row/Multi-table) | Single-document ACID / Eventual consistency | Tunable consistency (Row-level atomicity) |
| **Scaling** | Vertical scale-up; Sharding requires custom logic | Native horizontal scaling out-of-the-box | Massive horizontal write scale across rings |
| **Query Flexibility**| Complex JOINs, Aggregations, Subqueries | Key lookups, filtered indexes | Queries restricted by partition key structure |
| **Ideal For** | Financial ledgers, ERP, E-commerce checkouts | User profiles, catalogs, dynamic configurations | Metrics, chat logs, time-series, IoT telemetry |

---

## 🌲 2. Storage Engines & Indexing: B+ Trees vs LSM-Trees

Understanding how storage engines write to disk is the secret to answering database scale questions.

```mermaid
flowchart TD
    subgraph BTree["B+ Tree (Read-Optimized: MySQL / Postgres)"]
        direction TB
        BT1["1. Root & Internal Nodes<br/>(Page Pointers)"]
        BT2["2. Leaf Nodes (Doubly Linked)<br/>(Data Rows)"]
        BT3["3. In-Place Random Writes<br/>(Overwrites Pages)"]
        BT1 --> BT2 --> BT3
    end

    subgraph LSMTree["LSM-Tree (Write-Optimized)"]
        direction TB
        LSM1["1. Append to Memtable (RAM)<br/>+ Commit Log (WAL)"]
        LSM2["2. Flush to Disk SSTables<br/>(Immutable Sequential)"]
        LSM3["3. Background Compactions<br/>(Merge & Purge Keys)"]
        LSM1 --> LSM2 --> LSM3
    end
```

| Dimension | B+ Tree (RDBMS) | LSM-Tree (Log-Structured Merge Tree) |
|---|---|---|
| **Write Path** | Random I/O (updates disk pages in place) | **Sequential I/O** (appends to Memtable + WAL) |
| **Write Amplification**| High (modifying 1 byte rewrites whole 16KB page) | Moderate (deferred to background compaction) |
| **Read Path** | Directly traverses balanced tree index to page | Checks Memtable $\rightarrow$ Bloom Filters $\rightarrow$ SSTables |
| **Read Amplification** | Very Low (single page fetch) | Higher (may check multiple SSTable files on disk) |
| **Best Workload** | Read-heavy workloads, transactional systems | Write-heavy workloads, append-only logs, event streaming |

---

## 🔄 3. Database Replication Models

```mermaid
flowchart LR
    subgraph SingleLeader["1. Single Leader (Master-Slave)"]
        L1["Primary Node<br/>(Writes)"] -->|Async / Sync Replication| R1["Replica 1<br/>(Reads)"]
        L1 -->|Async Replication| R2["Replica 2<br/>(Reads)"]
    end

    subgraph MultiLeader["2. Multi-Leader (Multi-Master)"]
        ML1["Primary DC 1<br/>(Writes)"] <-->|Bi-directional Sync| ML2["Primary DC 2<br/>(Writes)"]
    end

    subgraph Leaderless["3. Leaderless (Dynamo / Cassandra)"]
        Client["Coordinator"] -->|Quorum Write| N1["Node A"]
        Client -->|Quorum Write| N2["Node B"]
        Client -->|Quorum Write| N3["Node C"]
    end
```

---

## ⚖️ 4. CAP Theorem vs PACELC Theorem

### The CAP Theorem
In a distributed asynchronous network, when a **Network Partition (P)** occurs, a system must trade off between **Consistency (C)** and **Availability (A)**:

```mermaid
flowchart TD
    CAP["CAP Theorem"] --> CP["CP (Consistency + Partition Tolerance)<br/>(Zookeeper, etcd, Spanner)<br/>Rejects writes during network split"]
    CAP --> AP["AP (Availability + Partition Tolerance)<br/>(Cassandra, DynamoDB)<br/>Accepts writes during split"]
    CAP --> CA["CA (Consistency + Availability)<br/>⚠️ Impossible across networks"]
```

### The PACELC Theorem (The Modern Extension)
CAP only considers what happens during network partitions. PACELC explains behavior during normal operations:

$$\text{If } \mathbf{P} \text{ (Partition) } \rightarrow \mathbf{A} \text{ vs } \mathbf{C}, \quad \mathbf{E} \text{ (Else) } \rightarrow \mathbf{L} \text{ (Latency) } \text{ vs } \mathbf{C} \text{ (Consistency)}$$

- **PC/EC** (e.g. Spanner, Postgres): Consistent during partitions; chooses Consistency over Latency normally.
- **PA/EL** (e.g. DynamoDB, Cassandra with low consistency): Available during partitions; chooses Low Latency over Strong Consistency normally.

---

## 🏛️ 5. ACID vs BASE Transactions

| ACID (Traditional SQL) | BASE (Distributed NoSQL) |
|---|---|
| **Atomicity**: All or nothing transaction execution | **Basically Available**: System guarantees availability |
| **Consistency**: Enforces schema constraints & invariants | **Soft State**: State may change over time without inputs |
| **Isolation**: Concurrent transactions do not interfere | **Eventual Consistency**: Given time, all nodes converge |
| **Durability**: Committed data persists across power loss | Focuses on speed and partition tolerance over immediate sync |
