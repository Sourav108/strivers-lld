# High-Level Design: Google Docs Collaborative Editor

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    UserA["User A Client (Typing)"] -->|WebSocket Sync| DocGW["Collaboration Gateway Fleet (WebSockets)"]
    UserB["User B Client"] -->|WebSocket Sync| DocGW

    subgraph CollabEngine["Real-Time Document Session Engine"]
        DocGW --> DocServer["Active Document Server Instance<br/>(Consistent Hashing by doc_id)"]
        DocServer <--> InMemState["In-Memory Document State & Operation Log"]
        DocServer --> LockCoord["Lock Coordinator (Redis / Zookeeper)"]
    end

    subgraph PersistencePipeline["Persistence & History Tier"]
        DocServer --> Kafka["Kafka Changelog Queue (Topic: doc_mutations)"]
        Kafka --> MutationWorker["Mutation Compaction Worker"]
        MutationWorker --> SnapshotDB[("Document Snapshots DB (Postgres / Bigtable)")]
        MutationWorker --> S3ColdStore["Amazon S3 (Full Revision History)"]
    end

    DocServer -->|Broadcast Transformed Op| DocGW
```

---

## 🔀 2. Conflict Resolution: Operational Transformation (OT) vs CRDTs

When User A and User B concurrently insert characters at position 3, a standard write overwrite causes text corruption.

```mermaid
flowchart TD
    subgraph OT["1. Operational Transformation (OT - Google Docs)"]
        OT1["Central Server acts as authoritative sequencer"]
        OT2["Operations transformed against previous operations: T(OpA, OpB)"]
        OT3["Low memory overhead, requires central server"]
    end

    subgraph CRDT["2. Conflict-Free Replicated Data Types (CRDTs - Figma)"]
        CR1["Peer-to-peer commutative data structures (LSEQ, Yjs, Automerge)"]
        CR2["Mathematical guarantee that all operations merge deterministically"]
        CR3["Zero central server needed; higher memory metadata overhead"]
    end
```

### Operational Transformation (OT) in Action:
- Original Text: `"CAT"`
- User A inserts `'H'` at index 0 $\rightarrow$ `OpA = Insert('H', 0)` $\rightarrow$ `"HCAT"`
- User B inserts `'S'` at index 3 $\rightarrow$ `OpB = Insert('S', 3)` $\rightarrow$ `"CATS"`
- **OT Transformation**:
  - The server transforms `OpB` against `OpA`: since `OpA` shifted text by 1 index, `OpB'` becomes `Insert('S', 4)`.
  - Both screens converge to: **`"HCATS"`**!
