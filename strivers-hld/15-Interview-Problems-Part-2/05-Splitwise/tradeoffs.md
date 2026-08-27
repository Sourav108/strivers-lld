# Trade-offs & Deep Dive: Splitwise

## ⚖️ 1. Real-Time Simplification vs On-Demand Simplification

| Approach | Latency Impact | System Cost | Recommendation |
|---|---|---|---|
| **Eager Real-Time Simplification** | Adds CPU latency to every expense insert | Locks entire group ledger during calculation | ❌ Avoid for high-traffic groups |
| **Lazy On-Demand Simplification** | 🚀 Zero write overhead on expense creation | Computes debt graph only when user clicks "Settle Up" | ✅ **Optimal Architecture Choice** |

---

## 🔒 2. Concurrency & Race Condition Prevention

- **Problem**: Alice and Bob simultaneously settle balances within the same group, potentially causing inconsistent split records.
- **Mitigation**:
  - Use **Database Row-Level Locking** (`SELECT ... FOR UPDATE`) or **Optimistic Concurrency Control with Version Numbers (`version_id`)** on the `group_balances` table.
