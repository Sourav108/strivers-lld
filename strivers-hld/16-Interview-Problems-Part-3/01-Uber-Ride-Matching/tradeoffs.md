# Trade-offs & Deep Dive: Uber Ride Matching

## ⚖️ 1. In-Memory Geospatial Index vs SQL `PostGIS`

| Dimension | SQL PostGIS (`ST_DWithin`) | In-Memory Spatial Index (Redis GEO / Uber H3) |
|---|---|---|
| **Write Throughput** | 🔴 Chokes at $> 5,000 \text{ writes/sec}$ (Disk WAL lock contention) | 🟢 **1,000,000+ writes/sec** in RAM |
| **Query Latency** | 30ms – 100ms | 🚀 **< 2ms** |
| **Recommendation** | Use PostGIS for static store locators | **Use In-Memory H3 / Redis for moving vehicles** |

---

## 🚨 2. Sharding Strategy: Sharding by City / Region

- **Partition Key**: Partition geospatial indexes by **City / Metropolitan Area** (e.g. `city:NYC`, `city:LONDON`).
- **Rationale**: A driver in London will never be matched with a rider in New York. Sharding by city naturally isolates failure blast radiuses and eliminates cross-city network queries.
