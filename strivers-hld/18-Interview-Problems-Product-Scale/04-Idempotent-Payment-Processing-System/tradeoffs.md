# Trade-offs & Deep Dive: Idempotent Payment Processing System

## ⚖️ 1. Two-Phase Commit (2PC) vs Saga Orchestration

| Dimension | Two-Phase Commit (2PC) | Orchestrated Saga (Temporal) |
|---|---|---|
| **Locking Mechanism** | Heavy cross-database row locks | **Zero cross-service locks** |
| **Failure Tolerance** | Blocks indefinitely if coordinator crashes | Highly fault-tolerant state machine with compensating steps |
| **Throughput Scaling**| Low ($< 200 \text{ txns/sec}$) | High ($> 10,000 \text{ txns/sec}$) |
| **Decision** | ❌ Not viable across microservices | ✅ **Industry Standard for Fintech** |
