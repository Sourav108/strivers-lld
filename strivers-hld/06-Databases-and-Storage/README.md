# 06 — Databases & Storage

## 🗄️ 1. SQL vs NoSQL: The Complete Decision Matrix

```mermaid
flowchart TD
    DB["Database Families"] --> SQL["Relational (SQL)<br/>PostgreSQL, MySQL<br/>- Strict Schema<br/>- ACID Transactions<br/>- Complex JOINs"]
    DB --> KV["Key-Value Store<br/>Redis, DynamoDB<br/>- Simple lookup by key<br/>- Sub-millisecond latency"]
    DB --> Doc["Document Store<br/>MongoDB, Couchbase<br/>- Flexible JSON/BSON<br/>- Nested hierarchies"]
    DB --> Col["Wide-Column / Columnar<br/>Cassandra, ScyllaDB, ClickHouse<br/>- Write-heavy scale<br/>- Time-series / Analytics"]
    DB --> Graph["Graph Database<br/>Neo4j, Amazon Neptune<br/>- Nodes & Edges<br/>- Social graphs, Fraud detection"]
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
    subgraph BTree["B+ Tree (Read-Optimized - MySQL InnoDB, Postgres)"]
        BT1["Balanced tree with all data in leaf nodes"]
        BT2["In-place random disk writes & page splits"]
        BT3["🚀 Ultra-fast point reads and range scans O(log N)"]
    end

    subgraph LSMTree["LSM-Tree (Write-Optimized - Cassandra, RocksDB)"]
        LSM1["Writes append to in-memory Memtable (RAM) + WAL"]
        LSM2["Flushed to immutable disk SSTables sequentially"]
        LSM3["Background Compaction merges SSTables"]
        LSM4["🚀 Blazing fast sequential write throughput"]
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
        L1["Master Node (Writes)"] -->|Async / Sync Replication| R1["Read Replica 1"]
        L1 -->|Async Replication| R2["Read Replica 2"]
    end

    subgraph MultiLeader["2. Multi-Leader (Multi-Master)"]
        ML1["Master DC 1 (Writes)"] <-->|Bi-directional Conflict Resolution| ML2["Master DC 2 (Writes)"]
    end

    subgraph Leaderless["3. Leaderless (Dynamo / Cassandra)"]
        Client["Client Coordinator"] -->|Write to Quorum| N1["Node A"]
        Client -->|Write to Quorum| N2["Node B"]
        Client -->|Write to Quorum| N3["Node C"]
    end
```

---

## ⚖️ 4. CAP Theorem vs PACELC Theorem

### The CAP Theorem
In a distributed asynchronous network, when a **Network Partition (P)** occurs, a system must trade off between **Consistency (C)** and **Availability (A)**:

```mermaid
flowchart TD
    CAP["CAP Theorem"] --> CP["CP (Consistency + Partition Tolerance)<br/>e.g. HBase, Zookeeper, etcd, MongoDB (majority)<br/>Rejects writes during network split to ensure no dirty reads."]
    CAP --> AP["AP (Availability + Partition Tolerance)<br/>e.g. Cassandra, DynamoDB, Couchbase<br/>Accepts writes during network split; returns stale data if needed."]
    CAP --> CA["CA (Consistency + Availability)<br/>⚠️ Impossible in distributed networks (Partitioning is unavoidable)"]
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
