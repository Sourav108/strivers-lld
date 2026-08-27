# High-Level Design: Distributed Key-Value Store

## 🏗️ 1. Masterless Ring Architecture

```mermaid
flowchart TD
    Client["Client App"] --> Coord["Coordinator Node (Any Node in Cluster)"]

    subgraph HashRing["Consistent Hash Ring (0 to 2^32-1)"]
        NodeA["Node A (Tokens: 0..25%)"]
        NodeB["Node B (Tokens: 25%..50%)"]
        NodeC["Node C (Tokens: 50%..75%)"]
        NodeD["Node D (Tokens: 75%..100%)"]

        NodeA <-->|Gossip Failure Detection| NodeB
        NodeB <-->|Gossip Failure Detection| NodeC
        NodeC <-->|Gossip Failure Detection| NodeD
        NodeD <-->|Gossip Failure Detection| NodeA
    end

    Coord -->|Quorum Write W=2| NodeA
    Coord -->|Quorum Write W=2| NodeB
    Coord -.->|Async Write| NodeC
```

---

## ⚙️ 2. Storage Node Internals: LSM-Tree & SSTables
- **Write Path**: Appends to Commit Log (WAL) on disk + In-memory Memtable (RAM) $\rightarrow$ Returns 200 OK in $< 1\text{ms}$.
- **Flush & Compaction**: Memtable flushes to immutable SSTable files on disk; background compactions merge duplicates.
- **Read Path**: Checks Memtable $\rightarrow$ Bloom Filter $\rightarrow$ SSTables on disk.
