# Requirements: Design Uber / Ride Hailing Matching System

## 📋 Functional Requirements (FR)
1. **Real-Time Driver Location Updates**: Active drivers stream their GPS coordinates (`lat`, `lon`) every 4 seconds.
2. **Find Nearby Drivers**: Riders can view nearby available drivers within a radius (e.g. 5km) on a live map.
3. **Ride Request & Matchmaking**: A rider requests a ride; the system finds the optimal nearby driver and dispatches the ride request.
4. **Driver Acceptance & Trip Tracking**: Driver accepts the request; both parties track trip progress in real-time.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency Geospatial Queries**: Return nearby drivers in **`< 50ms`**.
2. **High Write Throughput**: Ingest millions of concurrent driver location pings per second.
3. **High Availability**: 99.999% uptime for core matching and dispatching.
4. **Strong Consistency for Dispatch Match**: Exactly one driver must be matched per ride (no double assignment).
