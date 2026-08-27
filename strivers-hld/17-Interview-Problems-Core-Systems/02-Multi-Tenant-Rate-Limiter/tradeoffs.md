# Trade-offs & Deep Dive: Multi-Tenant Rate Limiter

## ⚖️ 1. Fail-Open vs Fail-Closed Policy

| Policy | Behavior on Cache Downtime | Business Impact | Decision |
|---|---|---|---|
| **Fail-Closed** | Block 100% of requests with 500 error | 🔴 Immediate revenue loss & global outage | ❌ Unacceptable |
| **Fail-Open** | Allow traffic through, fire alerts | 🟢 Preserves user experience and revenue | ✅ **Chosen Staff Strategy** |

---

## ⚡ 2. Local In-Memory vs Centralized Redis Check
- For ultra-high QPS endpoints (e.g. static CDN requests), perform **Local In-Memory Token Bucket** on the edge gateway.
- For billable API key calls, execute **Atomic Lua scripts in Redis** for global accuracy.
