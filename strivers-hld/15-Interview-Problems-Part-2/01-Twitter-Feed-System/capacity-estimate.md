# Capacity Estimation: Twitter / News Feed System

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **300 Million**
  - Tweets posted per user per day: **2 tweets**
  - Total Daily Tweets = $300\text{M} \times 2 = \mathbf{600 \text{ Million tweets/day}}$
  - Timeline refreshes per user per day: **10 refreshes**
  - Total Daily Timeline Reads = $300\text{M} \times 10 = \mathbf{3 \text{ Billion reads/day}}$

### Write (Tweet) QPS:
$$\text{Write QPS} = \frac{600 \times 10^6}{10^5} = \mathbf{6,000 \text{ tweets/sec}} \quad (\text{Peak: } 12,000 \text{ QPS})$$

### Read (Feed) QPS:
$$\text{Read QPS} = \frac{3 \times 10^9}{10^5} = \mathbf{30,000 \text{ reads/sec}} \quad (\text{Peak: } 60,000 \text{ QPS})$$

---

## 💾 2. Storage Estimation (5-Year Horizon)

- **Tweet Record**:
  - `tweet_id` (8 B), `user_id` (8 B), `text` (280 B), `media_urls` (64 B), `created_at` (8 B) $\approx 400 \text{ Bytes/record}$.
- **Daily Tweet Text Storage**:
$$\text{Daily Storage} = 600 \times 10^6 \times 400 \text{ Bytes} = \mathbf{240 \text{ GB/day}}$$
- **5-Year Tweet Storage**:
$$\text{5-Year Storage} = 240 \text{ GB} \times 365 \times 5 \approx \mathbf{438 \text{ TB}}$$

---

## ⚡ 3. Memory / Feed Cache Sizing (80/20 Rule)
- Store the top **800 Tweet IDs** per active user in a **Redis Sorted Set (`ZSET`)**:
- Memory per user timeline = $800 \times 8 \text{ Bytes (Tweet ID)} \approx \mathbf{6.4 \text{ KB}}$.
- For 300 Million Active Users:
$$\text{Total Timeline RAM} = 300 \times 10^6 \times 6.4 \text{ KB} \approx \mathbf{1.92 \text{ Terabytes (TB) RAM}}$$
*(A cluster of 16 Redis nodes with 128GB RAM each provides ample capacity).*
