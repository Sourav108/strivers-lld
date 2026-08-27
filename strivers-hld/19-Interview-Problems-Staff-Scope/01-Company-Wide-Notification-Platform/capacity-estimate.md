# Capacity Estimation: Company-Wide Notification Platform

## 🔢 1. Aggregate Volume
- 40 Internal Engineering Teams $\rightarrow$ **200 Million notifications/day**:
  - Push: 75% (150M/day)
  - Email: 15% (30M/day)
  - SMS: 10% (20M/day)
- Average Ingestion QPS = $\mathbf{2,000 \text{ notifications/sec}}$ (Peak Marketing Spike: $\mathbf{15,000 \text{ QPS}}$).

---

## 💾 2. 90-Day Multi-Tenant Audit Storage
- 200M/day $\times$ 90 days $\times$ 150 Bytes = $\mathbf{2.7 \text{ Terabytes (TB)}}$ (Stored in ClickHouse / OpenSearch for tenant usage dashboards).
