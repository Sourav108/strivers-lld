# Requirements: Design a Distributed Cache System (Redis/Memcached Cluster)

## 📋 Functional Requirements (FR)
1. **Key-Value Operations**: `get(key)`, `put(key, value, ttl)`, and `delete(key)`.
2. **Eviction Policies**: Support **LRU (Least Recently Used)** and **LFU (Least Frequently Used)** eviction when memory capacity is reached.
3. **Data Expiration**: Automatically expire and delete keys once their TTL has elapsed.
4. **Data Sizing**: Support arbitrary string/binary objects up to 1 MB per value.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Sub-Millisecond Latency**: Read and write latency under **`< 1ms`** (p99).
2. **High Availability & Fault Tolerance**: Cluster continues serving if an individual cache node fails.
3. **Horizontal Scalability**: Add or remove cache nodes dynamically with minimal key movement.
4. **Consistency**: Strong consistency or tunable bounded eventual consistency across cache replicas.
