# Capacity Estimation: Real-Time Ride-Matching Engine

## 🔢 1. Throughput Estimates
- 5 Million Active Drivers / 4 seconds = $\mathbf{1,250,000 \text{ GPS updates/sec}}$ (Peak: $2,500,000 \text{ QPS}$).
- 50 Million Daily Ride Requests $\approx \mathbf{500 \text{ dispatch requests/sec}}$ (Peak: $2,000 \text{ QPS}$).

---

## 💾 2. In-Memory Sizing
- 5M Drivers $\times 36 \text{ Bytes}$ location metadata $\approx \mathbf{180 \text{ MB}}$.
- In-memory Uber H3 / Google S2 spatial index in Redis $\approx \mathbf{2 \text{ GB RAM}}$ per city cluster.
- Network Ingress Bandwidth = $1.25\text{M} \times 36\text{ B} \approx \mathbf{45 \text{ MB/sec (360 Mbps)}}$.
