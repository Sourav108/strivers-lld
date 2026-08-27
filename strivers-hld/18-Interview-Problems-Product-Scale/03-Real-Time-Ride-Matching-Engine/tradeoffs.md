# Trade-offs & Deep Dive: Real-Time Ride-Matching Engine

## ⚖️ 1. In-Memory Hexagonal Spatial Index vs PostGIS SQL

| Dimension | Disk-Based PostGIS (`ST_DWithin`) | In-Memory Uber H3 Hex Index |
|---|---|---|
| **Write Throughput** | 🔴 Chokes at $> 5,000 \text{ writes/sec}$ | 🟢 **1,000,000+ writes/sec in RAM** |
| **Query Latency** | 30ms – 80ms | 🚀 **< 2ms** |
| **City Isolation** | Complex table sharding | Natural spatial partitioning by City/Hex |
| **Decision** | ❌ Not viable for real-time vehicles | ✅ **Mandatory for High-Scale Dispatch** |
