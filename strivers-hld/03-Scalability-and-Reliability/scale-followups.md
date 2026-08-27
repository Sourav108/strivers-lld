# Scale Follow-ups: Scalability, Reliability & Failure Domains

## 🚀 1. What Changes at 10x Scale?
- **Cascading Failure Amplification**: When an overloaded service responds with latency $> 2\text{s}$, upstream callers queue requests, exhausting worker thread pools across the entire cluster.
- **Remedy**: Enforce **strict timeout budgets** (propagated via gRPC context headers), deadline propagation, and **adaptive concurrency limits** (TCP Vegas-style bandwidth-delay product limits).

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Failure Domain Boundaries**: Ensure that a failure in one Availability Zone (AZ) or Region has **zero blast radius** on other regions.
- **Cell-Based Architecture**: Divide massive global fleets into independent, isolated **Cells** (e.g. 1 Cell = 100k users). If a catastrophic software bug or corrupted database migration hits production, only 1 Cell fails (0.1% blast radius) rather than the entire global customer base.
