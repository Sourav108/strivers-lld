# Scale Follow-ups: Failure Engineering & Blast-Radius Containment

## 🚀 1. What Changes at 10x Scale?
- **Thundering Herd during Service Restarts**: When a crashed service comes back online, thousands of queued clients hammer it immediately, instantly knocking it down again.
- **Solution**: Implement **Exponential Backoff with Full Jitter**:
  $$\text{Sleep} = \text{random}(0, \, \min(\text{MaxSleep}, \, \text{Base} \times 2^{\text{retry\_attempt}}))$$
  coupled with warmup readiness gates before adding restarted pods back into load balancer pools.

---

## 🌍 2. What Changes at 100x Scale?
- **Cell-Based Architecture**:
  - Partition global infrastructure into hundreds of independent, self-contained **Cells** (independent VPCs with isolated compute, Redis, and database shards).
  - A global routing service routes users to their specific cell.
  - A catastrophic bug or bad deploy destroys at most 1 cell ($\le 0.5\%$ of global users), preventing company-wide outages.
