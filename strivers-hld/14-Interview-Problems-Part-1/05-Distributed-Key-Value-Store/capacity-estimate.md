# Capacity Estimation: Distributed Key-Value Store

## 🔢 1. Throughput & QPS Estimates

- **Assumptions**:
  - Total Keys Stored: **10 Billion keys**
  - Average Value Size: **1 KB**
  - Read-to-Write Ratio: **10 : 1**
  - Daily Writes = $100 \text{ Million writes/day}$
  - Daily Reads = $1 \text{ Billion reads/day}$

### Throughput Calculations:
$$\text{Write QPS} = \frac{10^8}{10^5} = \mathbf{1,000 \text{ writes/sec}} \quad (\text{Peak: } 2,000 \text{ QPS})$$
$$\text{Read QPS} = \frac{10^9}{10^5} = \mathbf{10,000 \text{ reads/sec}} \quad (\text{Peak: } 20,000 \text{ QPS})$$

---

## 💾 2. Storage Estimation (Replication Factor $N = 3$)

- **Raw Unreplicated Data**:
$$\text{Raw Storage} = 10 \times 10^9 \times 1 \text{ KB} = \mathbf{10 \text{ Terabytes (TB)}}$$

- **Total Replicated Storage ($N = 3$)**:
$$\text{Replicated Storage} = 10 \text{ TB} \times 3 = \mathbf{30 \text{ TB}}$$

- **Cluster Sizing**:
  - If each storage node provides **2 TB of NVMe SSD storage**:
  - Number of Storage Nodes = $\frac{30 \text{ TB}}{2 \text{ TB}} = \mathbf{15 \text{ Storage Nodes}}$ across the distributed ring.
