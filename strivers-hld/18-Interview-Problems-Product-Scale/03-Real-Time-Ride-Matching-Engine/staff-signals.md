# Staff Signals: Real-Time Ride-Matching Engine

## 🎯 Staff-Level Grading Criteria:
- **Geospatial Data Structure Selection**: Articulates mathematical superiority of H3 hexagons over QuadTrees and square Geohashes.
- **Race Condition Prevention**: Solves double-dispatch using short-lived distributed lock leases (`Redlock` with 15s TTL).
- **City Failure Domain Isolation**: Partitions Kafka topics, Redis spatial caches, and dispatch engines strictly by Metropolitan Area to isolate blast radiuses.
