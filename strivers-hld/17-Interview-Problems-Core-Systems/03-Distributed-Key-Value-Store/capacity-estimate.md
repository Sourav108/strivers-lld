# Capacity Estimation: Distributed Key-Value Store

## 🔢 1. Throughput Estimates
- **Write QPS**: **50,000 writes/sec** (Peak: **100,000 QPS**).
- **Read QPS**: **500,000 reads/sec** (Peak: **1,000,000 QPS**).

---

## 💾 2. Storage Estimates (Replication $N=3$)
- 10 Billion Keys $\times 1\text{ KB} = \mathbf{10 \text{ TB raw}}$.
- Total Replicated Storage ($N=3$) = $\mathbf{30 \text{ TB}}$.
- Cluster Size = 15 Nodes (2TB NVMe SSD per node).
