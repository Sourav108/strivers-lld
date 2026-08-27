# Capacity Estimation: Distributed Rate Limiter

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **50 Million active users**
  - Average API requests per user per day: **20 requests**
  - Total Daily Requests = $50\text{M} \times 20 = \mathbf{1 \text{ Billion requests/day}}$

### Request QPS:
$$\text{Average QPS} = \frac{10^9}{10^5} = \mathbf{10,000 \text{ req/sec}}$$
$$\text{Peak QPS} = 10,000 \times 2 = \mathbf{20,000 \text{ req/sec}}$$

---

## 💾 2. Memory / Redis Storage Estimation

- We store rate limit counters using the **Sliding Window Counter** algorithm.
- **Key-Value Size per User**:
  - `Key`: `rl:user_123456:min` (24 bytes)
  - `Values`: `prev_counter` (4 bytes), `curr_counter` (4 bytes), `last_updated` (8 bytes)
  - Total per active user key $\approx \mathbf{64 \text{ Bytes}}$ (including Redis dict overhead)

### Peak Active Concurrent Keys in Memory:
- If 10 Million users are active within a 1-hour window:
$$\text{Total Memory} = 10 \times 10^6 \times 64 \text{ Bytes} \approx \mathbf{640 \text{ MB}}$$
- Adding a $3\times$ safety factor for Redis hashtable overhead:
$$\text{Total Redis RAM} \approx \mathbf{2 \text{ GB RAM}}$$
*(A single small Redis replica set easily holds all active rate limiter counters in RAM).*
