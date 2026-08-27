# Capacity Estimation: Uber Ride Matching

## 🔢 1. Driver Location Traffic & QPS

- **Assumptions**:
  - Total Active Drivers globally: **5 Million active drivers**
  - Drivers broadcast GPS coordinates every **4 seconds**
  - Total Active Riders: **50 Million riders**

### Ingestion (Write) QPS:
$$\text{Location Ingestion QPS} = \frac{5,000,000 \text{ drivers}}{4 \text{ seconds}} = \mathbf{1,250,000 \text{ location updates/sec}}$$
$$\text{Peak Write QPS} = 1.25\text{M} \times 2 = \mathbf{2,500,000 \text{ QPS}}$$

---

## 💾 2. In-Memory Geospatial Index RAM Sizing

- **Driver Location Record**:
  - `driver_id` (8 B), `lat` (8 B), `lon` (8 B), `status` (4 B), `updated_at` (8 B) $\approx 36 \text{ Bytes/driver}$.
- **Geohash Index RAM (5M Active Drivers)**:
$$\text{Active RAM} = 5 \times 10^6 \times 36 \text{ Bytes} \approx \mathbf{180 \text{ MB}}$$
*(With Redis geospatial GeoSet index overhead $\approx \mathbf{2 \text{ GB RAM}}$).*

---

## 🌐 3. Network Bandwidth
$$\text{Ingress Bandwidth} = 1.25 \times 10^6 \text{ req/s} \times 36 \text{ Bytes} \approx \mathbf{45 \text{ MB/second (360 Mbps)}}$$
