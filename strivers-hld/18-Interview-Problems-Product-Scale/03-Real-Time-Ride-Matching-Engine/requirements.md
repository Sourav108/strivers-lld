# Staff-Level Requirements: Real-Time Ride-Matching Engine

## 📋 The Staff Prompt
*"Design a real-time ride matching and dispatch engine (Uber / Lyft architecture) processing 2 Million driver GPS updates per second, geospatial indexing via hexagonal spatial partitions (Uber H3), and dynamic surge pricing with zero double-dispatch race conditions."*

---

## 🎯 Functional Requirements (FR)
1. **Driver GPS Ingestion**: High-frequency streaming from 5 Million active drivers every 4 seconds.
2. **Geospatial Proximity Queries**: Search available nearby drivers within radius in $< 20\text{ms}$.
3. **Dispatch & Matchmaking**: Lock candidate driver, send ride offer with 15s countdown, assign on acceptance.
4. **Dynamic Surge Pricing**: Real-time supply/demand ratio computation per geospatial hex cell.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Throughput**: 1.25 Million location updates/sec (Peak: 2.5M QPS).
2. **Zero Double-Dispatch**: Strong transactional consistency on driver assignment.
3. **High Availability**: 99.999% uptime for core matching loop.
