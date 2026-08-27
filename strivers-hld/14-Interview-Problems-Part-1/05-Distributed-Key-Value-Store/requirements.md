# Requirements: Design a Distributed Key-Value Store (Dynamo-Style)

## 📋 Functional Requirements (FR)
1. **Core Operations**:
   - `put(key, value)`: Stores a binary/string value associated with a key.
   - `get(key)`: Retrieves the value associated with a key.
   - `delete(key)`: Removes the key-value pair.
2. **Tunable Consistency**: Support configurable Read/Write Quorum consistency levels per query.
3. **Data Size**: Keys up to 256 bytes, values up to 10 MB.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **High Availability (AP Focus)**: Always accept writes, even during network partitions.
2. **Horizontal Scalability**: Scale throughput linearly by adding new commodity storage nodes.
3. **Low Latency**: Sub-10ms read/write latency.
4. **Zero Single Point of Failure**: Completely decentralized peer-to-peer ring topology.
