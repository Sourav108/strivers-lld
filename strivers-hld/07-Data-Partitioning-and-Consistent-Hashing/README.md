# 07 — Data Partitioning & Consistent Hashing

## 🔪 1. Database Sharding Strategies

When a dataset exceeds the storage or I/O capacity of a single database server, we partition it horizontally into **Shards**.

```mermaid
flowchart TD
    Sharding["Sharding Strategies"]
    
    subgraph DataPartitioning["Partitioning Approaches"]
        Range["1. Range-Based<br/>(Key ranges e.g. A-M, N-Z)"]
        Hash["2. Hash-Based<br/>(hash key modulo N)"]
        Directory["3. Directory / Lookup<br/>(Central metadata map)"]
        Geo["4. Geographic<br/>(EU vs US user residency)"]
    end

    Sharding --> DataPartitioning
```

| Strategy | Pros | Cons | Best Use Case |
|---|---|---|---|
| **Range-Based** | Easy to perform range queries (`BETWEEN '2026-01' AND '2026-06'`) | Massive **hot spotting** (recent dates get 100% writes) | Time-series archival data |
| **Hash-Based** | Uniformly distributes records across all shards | Range queries must query *all* shards (Scatter-Gather) | User profiles, account lookups |
| **Consistent Hashing** | Adding/removing nodes moves only $K/N$ keys | Requires virtual node management | Distributed caches (Redis, Memcached), DynamoDB |

---

## ⭕ 2. Consistent Hashing & Virtual Nodes

### The Problem with Naive Modulo Hashing
If we map keys to $N$ servers using `server_index = hash(key) % N`:
- When 1 server crashes ($N \rightarrow N-1$), almost **100% of keys remap to new servers**.
- In a cache cluster, this triggers a catastrophic **Cache Avalanche**, crashing the database origin.

### Consistent Hashing Ring Solution
Consistent Hashing maps both **Servers** and **Keys** onto a circular 32-bit hash ring ($0 \text{ to } 2^{32}-1$):

```mermaid
flowchart TD
    subgraph HashRing["Consistent Hash Ring (0 to 2^32-1)"]
        direction TB
        N1["Node A (Pos: 1000)"] -->|Clockwise Key Allocation| N2["Node B (Pos: 5000)"]
        N2 --> N3["Node C (Pos: 9000)"]
        N3 --> N1
    end
```

- **Lookup**: To find which node owns `Key_X`, compute `hash(Key_X)` and traverse **clockwise** until encountering the first server node.
- **Node Addition / Removal**: If Node B fails, only keys between Node A and Node B are re-assigned to Node C. All other keys remain unaffected! Only $K/N$ keys move.

### Virtual Nodes (Vnodes) for Even Load Balancing
If physical servers are placed unevenly on the ring, one server might handle 70% of the data (non-uniform distribution).

```mermaid
flowchart LR
    Physical["Server A"] --> V1["Vnode A-1"]
    Physical --> V2["Vnode A-2"]
    Physical --> V3["Vnode A-3"]
```

- Each physical server is assigned **100–300 virtual nodes** across the ring.
- Guarantees uniform key distribution and allows heterogeneous servers (a server with 2x RAM gets 2x virtual nodes).

---

## 🗳️ 3. Quorum Consensus ($N, R, W$)

In leaderless distributed databases (Cassandra, DynamoDB), consistency is configured per query using Quorum parameters:

- **$N$**: Replication Factor (Total copies of data across the cluster, e.g., $N = 3$).
- **$W$**: Write Quorum (Number of replicas that must acknowledge a write before returning success).
- **$R$**: Read Quorum (Number of replicas that must respond to a read query before returning data).

$$\mathbf{R + W > N} \implies \text{\textbf{Strong Consistency Guarantee}}$$

```mermaid
flowchart LR
    Client["Client Write"] --> Coord["Coordinator Node"]
    Coord -->|Write 1| R1["Replica 1 (Ack)"]
    Coord -->|Write 2| R2["Replica 2 (Ack)"]
    Coord -.->|Async Write 3| R3["Replica 3 (Pending)"]

    subgraph QuorumRule["Quorum Check (N=3, W=2, R=2)"]
        QR["W=2 Acks Received -> OK<br/>R + W (2 + 2 = 4) > 3<br/>Guarantees Latest Read"]
    end
```

---

## 🌸 4. Bloom Filters: Probabilistic Fast Lookups

A **Bloom Filter** is an ultra-compact, bit-array probabilistic data structure used to test whether an element is a member of a set.

```mermaid
flowchart TD
    Key["Input Key: 'user_123'"] --> H1["Hash 1 -> Bit 4"]
    Key --> H2["Hash 2 -> Bit 11"]
    Key --> H3["Hash 3 -> Bit 19"]

    subgraph BitArray["Bit Array in RAM"]
        B["Bits: 0 0 0 1 0 0 0 0 0 0 1 0 0 0 0 0 0 1"]
    end

    H1 & H2 & H3 --> BitArray
```

### The Invariable Guarantees of Bloom Filters:
- **If Bloom Filter returns FALSE**: The item is **definitely NOT in the dataset** (100% guarantee $\rightarrow$ Zero False Negatives). Bypasses expensive disk I/O!
- **If Bloom Filter returns TRUE**: The item is **PROBABLY in the dataset** (Possible False Positive due to hash collisions $\rightarrow$ Query disk SSTable to verify).

### Top HLD Use Cases:
1. **LSM-Tree DBs (Cassandra / RocksDB)**: Skips reading disk SSTables if key is not present.
2. **Web Crawlers**: Avoids re-crawling billions of URLs without storing all URL strings in RAM.
3. **CDN / Cache Shielding**: Prevents cache penetration attacks by blocking non-existent keys.
