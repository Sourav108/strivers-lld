# High-Level Design: Distributed Cache System

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Client["Application Client Cluster"] --> ClientLib["Smart Cache Client (Consistent Hashing Ring)"]

    subgraph CacheCluster["Distributed Cache Cluster (16 Hash Slots / Vnodes)"]
        subgraph Shard1["Shard 1 (Slot: 0..5460)"]
            Master1["Primary Cache Node 1"] --> Replica1["Replica Node 1"]
        end

        subgraph Shard2["Shard 2 (Slot: 5461..10922)"]
            Master2["Primary Cache Node 2"] --> Replica2["Replica Node 2"]
        end

        subgraph Shard3["Shard 3 (Slot: 10923..16383)"]
            Master3["Primary Cache Node 3"] --> Replica3["Replica Node 3"]
        end
    end

    ClientLib -->|Direct TCP GET/PUT| Master1
    ClientLib -->|Direct TCP GET/PUT| Master2
    ClientLib -->|Direct TCP GET/PUT| Master3

    subgraph ClusterCoordination["Cluster State & Failover"]
        GossipMesh["Gossip Protocol Failure Detection (Raft / Sentinel)"]
        Master1 <--> GossipMesh
        Master2 <--> GossipMesh
        Master3 <--> GossipMesh
    end
```

---

## 🧮 2. LRU Eviction: Doubly Linked List + Hash Map

To guarantee $O(1)$ time complexity for both `get()` and `put()` operations:

```mermaid
flowchart LR
    subgraph HashMap["Hash Map O(1) Lookup"]
        K1["Key: 'user_1'"] --> N1["Node 1"]
        K2["Key: 'user_2'"] --> N2["Node 2"]
        K3["Key: 'user_3'"] --> N3["Node 3"]
    end

    subgraph DoublyLinkedList["Doubly Linked List (Order of Access)"]
        Head["HEAD (Most Recently Used)"] <--> N1
        N1 <--> N2
        N2 <--> N3
        N3 <--> Tail["TAIL (Least Recently Used - Evict Target)"]
    end
```

- **`get(key)`**: Look up node in Hash Map $O(1)$. Move node to `HEAD` of Doubly Linked List $O(1)$.
- **`put(key, value)`**: If key exists, update value and move to `HEAD`. If new, insert at `HEAD`. If memory full, delete node at `TAIL` from both Doubly Linked List and Hash Map $O(1)$.

---

## ⏰ 3. TTL Expiration Strategies

1. **Passive / Lazy Expiration**: When a client calls `get(key)`, the server checks if `current_time > expiry_time`. If expired, it deletes the key and returns `nil`.
2. **Active Periodic Expiration**: A background timer runs 10 times per second, testing 20 random keys with TTLs. If $> 25\%$ are expired, it repeats the cycle to prevent stale memory leaks.
