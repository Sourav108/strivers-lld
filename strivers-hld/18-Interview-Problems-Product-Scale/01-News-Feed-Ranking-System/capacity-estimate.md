# Capacity Estimation: News Feed Ranking System

## 🔢 1. Traffic Estimates
- **DAU**: **500 Million users**.
- **Daily Posts**: $500\text{M} \times 2 = \mathbf{1 \text{ Billion posts/day}} \approx \mathbf{10,000 \text{ writes/sec}}$.
- **Daily Feed Reads**: $500\text{M} \times 10 = \mathbf{5 \text{ Billion feed reads/day}} \approx \mathbf{50,000 \text{ QPS}}$ (Peak: $100,000 \text{ QPS}$).

---

## 💾 2. Redis Feed Cache Memory Sizing
- Top 800 Post IDs per active user in Redis Sorted Sets (`ZSET`):
$$500 \times 10^6 \text{ users} \times 800 \times 8 \text{ Bytes} \approx \mathbf{3.2 \text{ Terabytes RAM}}$$
*(A 32-node Redis Cluster with 128GB RAM each provides ample room for feed caches and social graphs).*
