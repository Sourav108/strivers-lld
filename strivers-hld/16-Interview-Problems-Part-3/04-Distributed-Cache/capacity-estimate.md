# Capacity Estimation: Distributed Cache System

## 🔢 1. Throughput & QPS Estimates

- **Assumptions**:
  - Target Read QPS: **1 Million reads/sec**
  - Target Write QPS: **100,000 writes/sec**
  - Total Keys: **1 Billion keys**
  - Average Value Size: **1 KB**

### QPS Summary:
$$\text{Total Cache QPS} = 1,000,000 \text{ reads/s} + 100,000 \text{ writes/s} = \mathbf{1.1 \text{ Million QPS}}$$

---

## 💾 2. Cluster Memory (RAM) Estimation

- **Raw Data Volume**:
$$\text{Raw Cache Data} = 10^9 \text{ keys} \times 1 \text{ KB} = \mathbf{1 \text{ Terabyte (TB) RAM}}$$

- **Overhead & Replication ($N = 2$)**:
  - Redis metadata overhead per key $\approx 50 \text{ Bytes}$ ($50 \text{ GB}$).
  - Primary + 1 Replica per shard:
$$\text{Total RAM Required} = 1.05 \text{ TB} \times 2 = \mathbf{2.1 \text{ Terabytes RAM}}$$

- **Cluster Sizing**:
  - Using AWS `r6g.2xlarge` instances (**64 GB RAM** per instance):
  - Total Nodes = $\frac{2,100 \text{ GB}}{64 \text{ GB}} \approx \mathbf{33 \text{ Cache Instances}}$ (e.g., 16 Primaries + 16 Replicas).
