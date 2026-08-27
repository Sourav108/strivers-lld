# Trade-offs & Deep Dive: URL Shortener

## ⚖️ 1. HTTP 301 (Permanent) vs HTTP 302 (Temporary) Redirection

| Dimension | HTTP 301 Moved Permanently | HTTP 302 Found (Temporary Redirect) |
|---|---|---|
| **Browser Behavior** | Browser caches the long URL permanently in local memory. | Browser always queries the shortener server on every click. |
| **Server Load** | 🟢 Extremely low (Subsequent clicks bypass server). | 🟡 Higher (Every click hits redirection server/CDN). |
| **Analytics Accuracy** | 🔴 Inaccurate (Only the 1st click per user is recorded). | 🟢 **100% Accurate click tracking & telemetry.** |
| **Recommendation** | Use 301 only if analytics and monetization do not matter. | **Use 302 (or 307)** if tracking metrics and ad conversions. |

---

## 🚨 2. Bottlenecks & Failure Modes

### 1. Single Point of Failure in KGS (Key Generation Service)
- **Failure Risk**: If the active KGS server crashes with 10,000 in-memory keys, what happens?
- **Mitigation**: Pre-allocated keys are simply discarded on crash. Since $62^7 = 3.5\text{ Trillion}$ keys exist, losing 10,000 keys is $< 0.0000003\%$ of key space. A secondary standby KGS node immediately loads the next batch from the database.

### 2. Database Sharding Strategy
- **Partition Key**: Shard by `short_key` using **Consistent Hashing**.
- **Lookup Cost**: $O(1)$ directly mapped to the designated database shard.

### 3. Cache Purge & Expired Links Cleanup
- Instead of scanning billions of database rows continuously, use a **Lazy Cleanup + Background Batch Worker**:
  - When a user requests an expired key, return 404 and queue a delete event to Kafka.
  - A low-priority night batch job purges expired entries during low-traffic windows.
