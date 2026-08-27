# Trade-offs & Deep Dive: Distributed Rate Limiter

## ⚖️ 1. Centralized Redis vs Local In-Memory Rate Limiting

| Dimension | Centralized Redis Rate Limiting | Local In-Memory Rate Limiting (Per Gateway Node) |
|---|---|---|
| **Accuracy** | 🟢 **100% Globally Accurate** across all load balancers. | 🔴 Inaccurate (Requires dividing quota across dynamic nodes). |
| **Latency Overhead** | $\sim 1\text{ms}$ network RTT to Redis cluster. | 🚀 **< 0.05ms** (Pure RAM read on host). |
| **Failure Mode** | Redis downtime impacts rate limiting checks. | Independent node resilience. |
| **Recommendation** | Use **Centralized Redis** with local fallback for strict APIs. | Use local tokens for coarse-grained DDoS firewalling. |

---

## 🚨 2. Bottlenecks & Failure Modes

### 1. Redis Cluster Failure & Fail-Open Strategy
- **Risk**: If the Redis cluster crashes or experiences a network partition, should we block all traffic or let traffic pass?
- **Decision**: **Fail-Open Policy**. If Redis times out (> 5ms), the middleware logs an alert and allows the request through. It is better to risk higher backend load than to take down 100% of revenue-generating traffic.

### 2. Multi-Region Synchronization (Eventual Consistency vs Latency)
- For multi-region deployments (e.g. US-East and EU-West):
  - Checking a single global Redis across continents adds $150\text{ms}$ of latency per request.
  - **Solution**: Deploy local Redis clusters per region. Synchronize counters asynchronously using Kafka/CRDTs, allowing slight bursts during inter-region traffic shifts.
