# Capacity Estimation: Distributed Notification System

## 🔢 1. Throughput & QPS Estimates

- **Daily Volume**: **100 Million notifications/day**
  - Push Notifications: 70% (70M)
  - SMS: 10% (10M)
  - Email: 20% (20M)

### Ingestion QPS:
$$\text{Average QPS} = \frac{100 \times 10^6}{10^5} = \mathbf{1,000 \text{ notifications/sec}}$$
$$\text{Peak QPS (e.g. Flash Sales / Breaking News)} = 1,000 \times 5 = \mathbf{5,000 \text{ notifications/sec}}$$

---

## 💾 2. Storage Estimation (Log Retention 90 Days)

- **Log Record Size**:
  - `notification_id` (8 B), `user_id` (8 B), `channel` (8 B), `template_id` (8 B), `status` (8 B), `created_at` (8 B) $\approx 100 \text{ Bytes/record}$.
- **90-Day Audit Storage**:
$$\text{Storage} = 100\text{M/day} \times 90 \times 100 \text{ Bytes} = \mathbf{900 \text{ GB}}$$
*(Stored in sharded PostgreSQL / Cassandra / OpenSearch for operational auditing).*
