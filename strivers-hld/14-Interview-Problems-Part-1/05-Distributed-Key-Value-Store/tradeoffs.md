# Trade-offs & Deep Dive: Distributed Key-Value Store

## ⚖️ 1. Strong Consistency vs High Availability (AP vs CP)

| Quorum Configuration | Formula ($N=3$) | Guarantee | Best Use Case |
|---|---|---|---|
| **$W=1, R=1$** | $R + W \le N$ | 🚀 Fastest latency, Eventual Consistency | Activity feeds, Metrics logging |
| **$W=2, R=2$** | $R + W > N$ | 🟢 **Strong Consistency**, High Availability | User profiles, Session state |
| **$W=3, R=1$** | $R + W > N$ | 🚀 Fast reads, Slow/Fragile writes | Read-heavy product catalogs |

---

## 🚨 2. Handling Network Partitions & Split-Brain

### 1. Hinted Handoff (Temporary Outage Recovery)
- If Replica 3 is temporarily unreachable during a write, the coordinator saves the write locally as a "Hint".
- When Replica 3 recovers, the coordinator replays all stored hints.

### 2. Merkle Trees (Anti-Entropy Synchronization)
- To repair nodes that have been offline for days, nodes exchange binary **Merkle Trees** (cryptographic hash trees).
- Only the specific divergent tree branches are synchronized across the network, avoiding multi-gigabyte full table scans.
