# Trade-offs & Deep Dive: Notification System

## ⚖️ 1. Message Ordering vs Parallel Delivery

| Strategy | Ordering Guarantee | Delivery Throughput | Recommendation |
|---|---|---|---|
| **Strict FIFO Queue (Single Partition)** | 🟢 Strict ordering | 🔴 Bottlenecks at single consumer ($\sim 1\text{k req/s}$) | Use only for sequential financial workflows |
| **Partitioned by `user_id`** | 🟢 FIFO per individual user | 🚀 **Millions/sec across consumer fleet** | **Standard Architecture Choice** |

---

## 🚨 2. Preventing Duplicate Notifications (Idempotency)

- **Scenario**: A network timeout occurs after SendGrid accepts an email request.
- **Solution**:
  - The worker generates a unique **Deduplication Hash**: `SHA256(user_id + template_id + payload_hash)`.
  - Stored in Redis with `SET key NX EX 300` (5-minute deduplication window).
  - If identical requests arrive within 5 minutes, secondary requests are dropped.
