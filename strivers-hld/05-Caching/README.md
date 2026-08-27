# 05 — Caching Strategies & Distributed In-Memory Systems

## ⚡ 1. Caching Access Patterns

Caching is the single most effective way to reduce database read latency from `~15ms` to `< 1ms`.

```mermaid
flowchart TD
    subgraph CacheAside["1. Cache-Aside (Lazy Loading)"]
        CA_App["Application"] -->|1. Check Cache| CA_Cache["Cache"]
        CA_App -->|2. On Miss: Query DB| CA_DB["Database"]
        CA_App -->|3. Populate Cache| CA_Cache
    end

    subgraph WriteThrough["2. Write-Through"]
        WT_App["Application"] -->|1. Write Data| WT_Cache["Cache"]
        WT_Cache -->|2. Synchronous Write| WT_DB["Database"]
    end

    subgraph WriteBack["3. Write-Back / Write-Behind"]
        WB_App["Application"] -->|1. Write to Cache (Fast Ack)| WB_Cache["Cache"]
        WB_Cache -.->|2. Async Batch Flush| WB_DB["Database"]
    end

    subgraph WriteAround["4. Write-Around"]
        WA_App["Application"] -->|1. Write directly to DB| WA_DB["Database"]
        WA_App -.->|2. Subsequent Read on Miss| WA_Cache["Cache"]
    end
```

| Pattern | Write Latency | Read Latency | Consistency | Risk / Trade-off |
|---|---|---|---|---|
| **Cache-Aside** | Fast (Writes directly to DB, invalidates cache) | Fast on Hit, Slow on initial Miss | Eventual (Cache populated lazily) | Stale data if DB updated without invalidating cache |
| **Write-Through** | Slower (Two synchronous writes: Cache + DB) | 🚀 Fastest (Cache is always warm and up-to-date) | Strong between Cache and DB | Higher write latency, caches data that may never be read |
| **Write-Back** | 🚀 Fastest (Acknowledges immediately from RAM) | 🚀 Fastest | Eventual | 🚨 **Risk of data loss** if cache node crashes before async DB flush |
| **Write-Around** | Fast (Bypasses cache, writes only to DB) | Slower for newly written items | Eventual | Good for write-heavy data that is rarely read immediately |

---

## 🧹 2. Cache Eviction Policies

When cache memory fills up to its limit (e.g. `maxmemory` in Redis), the eviction policy decides which keys to purge:

```mermaid
flowchart LR
    Policies["Eviction Policies"] --> LRU["LRU (Least Recently Used)<br/>Evicts keys not accessed for the longest time<br/>(Doubly Linked List + Hash Map)"]
    Policies --> LFU["LFU (Least Frequently Used)<br/>Evicts keys with lowest access frequency counter"]
    Policies --> FIFO["FIFO (First In First Out)<br/>Evicts oldest key based on insertion time"]
    Policies --> TTL["Volatile-TTL<br/>Evicts keys with the shortest remaining TTL"]
```

---

## ⚔️ 3. Redis vs Memcached

```mermaid
flowchart LR
    subgraph Redis["Redis Architecture"]
        R1["Single-Threaded Event Loop (Redis 6+ I/O threads)"]
        R2["Rich Data Types: Strings, Hashes, Lists, Sets, Sorted Sets, HyperLogLog, Bitmaps, Streams"]
        R3["Persistence: RDB Snapshots & AOF Log"]
        R4["Replication: Master-Replica + Redis Sentinel / Cluster"]
        R5["Lua Scripting & Pub/Sub"]
    end

    subgraph Memcached["Memcached Architecture"]
        M1["Multi-Threaded Architecture (scales linearly with CPU cores)"]
        M2["Simple Key-Value Strings / Blobs only"]
        M3["Pure In-Memory (No persistence, restarts lose all data)"]
        M4["Client-Side Consistent Hashing"]
    end
```

| Feature | Redis | Memcached |
|---|---|---|
| **Threading Model** | Single-threaded core engine (No lock contention) | Multi-threaded (Efficient multi-core CPU scaling) |
| **Data Structures** | Rich (Strings, Lists, Sets, Sorted Sets, Hashes, Streams) | Simple Strings/Blobs only |
| **Persistence** | ✅ Yes (RDB snapshots + AOF append logs) | ❌ No (Purely volatile in-memory) |
| **Pub/Sub & Streaming** | ✅ Built-in Pub/Sub & Redis Streams | ❌ No |
| **Clustering** | ✅ Native Redis Cluster (16,384 Hash Slots) | Handled by client-side consistent hashing |

---

## 🚨 4. The 4 Cache Disasters & Production Mitigations

```mermaid
flowchart TD
    subgraph Disasters["Cache Failure Modes"]
        D1["1. Cache Penetration<br/>(Queries for non-existent keys bypass cache to DB)"]
        D2["2. Cache Breakdown / Stampede<br/>(Hot key expires -> 100k queries hit DB simultaneously)"]
        D3["3. Cache Avalanche<br/>(Massive number of keys expire at the exact same second)"]
        D4["4. Hot Spot Concurrency<br/>(Single celebrity key overwhelms single cache node)"]
    end

    subgraph Mitigations["Production Mitigations"]
        M1["🛡️ Bloom Filter at Gateway + Cache Null values with short TTL"]
        M2["🔒 Distributed Mutex Lock (Redlock) or Logical Expiry in Background"]
        M3["🎲 Add Random Jitter to TTL (e.g. TTL = Base + rand(0, 300s))"]
        M4["⚡ Local In-Memory Cache (Caffeine/Guava) or Key Replication (key:1, key:2)"]
    end

    D1 ==> M1
    D2 ==> M2
    D3 ==> M3
    D4 ==> M4
```

---

## 🌍 5. Content Delivery Networks (CDN)

A **CDN** is a globally distributed network of edge proxy servers (PoPs) that delivers static and cached dynamic content (images, videos, HTML, API responses) close to users.

```mermaid
sequenceDiagram
    autonumber
    actor User as User (London)
    participant Edge as CDN Edge PoP (London)
    participant Shield as Origin Shield CDN (Ireland)
    participant Origin as Origin API & S3 (US-East)

    User->>Edge: GET /static/video_chunk_1.ts
    alt Edge Cache Hit
        Edge-->>User: 200 OK (5ms Latency)
    else Edge Miss
        Edge->>Shield: Forward Request
        alt Shield Cache Hit
            Shield-->>Edge: Return Chunk
            Edge-->>User: 200 OK (20ms Latency)
        else Shield Miss
            Shield->>Origin: Fetch from Origin S3 Bucket
            Origin-->>Shield: Return Video Chunk (120ms Latency)
            Shield->>Shield: Cache Asset
            Shield-->>Edge: Forward Asset
            Edge->>Edge: Cache Asset (TTL 24h)
            Edge-->>User: 200 OK
        end
    end
```
