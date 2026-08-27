# Capacity Estimation: URL Shortener @ 1B Users

## 🔢 1. Traffic Estimates
- **Redirection Reads**: 100 Billion requests/month $\approx \mathbf{40,000 \text{ QPS}}$ (Peak: $100,000 \text{ QPS}$).
- **New Short URLs Created**: 1 Billion writes/month $\approx \mathbf{400 \text{ writes/sec}}$ (Peak: $1,000 \text{ QPS}$).

---

## 💾 2. 5-Year Storage Estimates
- 1 Billion URLs/month $\times 60 \text{ months} = \mathbf{60 \text{ Billion records}}$.
- Size per record = 500 Bytes.
$$\text{Total 5-Year Storage} = 60 \times 10^9 \times 500 \text{ Bytes} \approx \mathbf{30 \text{ Terabytes (TB)}}$$

---

## ⚡ 3. Memory / Cache Sizing (80/20 Rule)
- Daily Active Reads = $\frac{100\text{B}}{30} \approx 3.3 \text{ Billion reads/day} \times 500\text{ B} = 1.65 \text{ TB/day}$.
- Cache 20% of daily active read links:
$$\text{Cache RAM} = 1.65 \text{ TB} \times 0.20 = \mathbf{330 \text{ GB RAM}}$$
*(A 6-node Redis Cluster with 64GB RAM each across multi-AZ provides high availability and $< 1\text{ms}$ read latency).*
