# Capacity Estimation: Pastebin

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - New pastes created: **10 Million pastes per day**
  - Read-to-Write Ratio: **10 : 1** (100 Million paste reads per day)
  - Average paste text size: **10 KB**

### Write QPS:
$$\text{Write QPS} = \frac{10 \times 10^6}{10^5} = \mathbf{100 \text{ pastes/sec}} \quad (\text{Peak: } 200 \text{ QPS})$$

### Read QPS:
$$\text{Read QPS} = \frac{100 \times 10^6}{10^5} = \mathbf{1,000 \text{ reads/sec}} \quad (\text{Peak: } 2,000 \text{ QPS})$$

---

## 💾 2. Storage Estimation (5-Year Horizon)

- **Daily Storage Ingestion**:
$$\text{Daily Storage} = 10 \times 10^6 \times 10 \text{ KB} = \mathbf{100 \text{ GB/day}}$$

- **5-Year Storage Volume**:
$$\text{5-Year Storage} = 100 \text{ GB} \times 365 \times 5 \approx \mathbf{182.5 \text{ TB}}$$

- **Metadata Database Size**:
  - `paste_id` (7 bytes), `user_id` (8 bytes), `s3_object_key` (64 bytes), `created_at` (8 bytes), `expires_at` (8 bytes) $\approx 100 \text{ Bytes/record}$.
  - 5-Year Metadata Count = $10\text{M/day} \times 1825 \approx \mathbf{18.25 \text{ Billion records}}$.
  - Metadata DB Storage = $18.25 \times 10^9 \times 100 \text{ B} \approx \mathbf{1.82 \text{ TB}}$.

---

## ⚡ 3. Memory / Cache Estimation (80/20 Rule)
- Daily Read Volume = $100 \times 10^6 \times 10 \text{ KB} = \mathbf{1 \text{ TB/day}}$
- **Cache 20% of Daily Active Read Pastes**:
$$\text{Cache RAM} = 1 \text{ TB} \times 0.20 = \mathbf{200 \text{ GB RAM}}$$
*(A 3-node Redis cluster with 128GB RAM each provides ample headroom).*
