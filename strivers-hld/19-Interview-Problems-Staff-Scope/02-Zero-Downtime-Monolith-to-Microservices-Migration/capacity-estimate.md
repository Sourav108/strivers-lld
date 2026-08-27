# Capacity Estimation: Monolith to Microservices Migration

## 🔢 1. Migration Throughput Estimates
- **Active Orders QPS**: 2,000 writes/sec (Peak: 5,000 QPS).
- **Existing Monolith Database Size**: 20 Terabytes (PostgreSQL).
- **Historical Orders to Migrate**: 500 Million rows.

---

## 💾 2. CDC Replication Bandwidth & Time Sizing
- Initial Snapshot Sync at 50 MB/sec:
$$\text{Migration Time} = \frac{20 \text{ TB}}{50 \text{ MB/sec}} \approx \mathbf{111 \text{ Hours (4.6 Days of continuous background sync)}}$$
- Kafka WAL Streaming Ingress Bandwidth = $5,000 \text{ ops/sec} \times 2\text{ KB} = \mathbf{10 \text{ MB/sec}}$.
