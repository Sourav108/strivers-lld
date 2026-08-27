# Trade-offs & Deep Dive: Distributed Key-Value Store

## ⚖️ 1. Quorum Consistency vs Latency Trade-offs

| Quorum Parameter ($N=3$) | Read Latency | Write Latency | Consistency | Failure Tolerance |
|---|---|---|---|---|
| **$W=1, R=1$** | 🚀 **< 1ms** | 🚀 **< 1ms** | Eventual Consistency | Survives 2 node crashes |
| **$W=2, R=2$** | $\sim 2\text{ms}$ | $\sim 2\text{ms}$ | **Strong Consistency** | Survives 1 node crash |
| **$W=3, R=1$** | 🚀 **< 1ms** | Slower ($\sim 5\text{ms}$) | **Strong Consistency** | 0 write failures tolerated |
