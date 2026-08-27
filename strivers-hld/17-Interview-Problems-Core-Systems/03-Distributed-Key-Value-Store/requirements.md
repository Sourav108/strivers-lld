# Staff-Level Requirements: Distributed Key-Value Store

## 📋 The Staff Prompt
*"Design a globally distributed masterless Key-Value store (Dynamo/Cassandra architecture) that provides linear write scalability, tunable read/write consistency (N, R, W), sub-5ms p99 latency, and automatic self-healing under network partitions."*

---

## 🎯 Functional Requirements (FR)
1. **Core Operations**: `put(key, value)`, `get(key)`, `delete(key)` supporting values up to 10 MB.
2. **Tunable Quorums**: Allow clients to specify `ONE`, `QUORUM`, or `ALL` per query.
3. **Partitioning**: Automated consistent hashing across a dynamic ring of storage nodes.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **High Availability (AP Focus)**: Continuous write availability even during partial network partitions.
2. **Sub-5ms Latency**: In-memory Memtable + LSM-tree disk storage.
3. **Anti-Entropy & Self-Healing**: Automated Merkle tree synchronization and Hinted Handoff.
