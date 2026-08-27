# Scale Follow-ups: Distributed Systems & Consensus

## 🚀 1. What Changes at 10x Scale?
- **Raft Consensus Leader Bottleneck**: In Raft/etcd, all writes pass through the single elected Leader. When write volume exceeds 10,000 writes/sec, the leader CPU and disk WAL become saturated.
- **Solution**: Implement **Multi-Raft / Partitioned Consensus** (as in CockroachDB and TiKV), where the keyspace is split into hundreds of independent small Raft consensus groups.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Multi-Region Consensus Latency**: A standard 3-node Raft cluster spread across US, Europe, and Asia requires cross-continental RTT ($150\text{ms}$) on *every single write quorum*.
- **Solution**: Use **Hierarchical Quorums / Local Paxos Groups** with hardware synchronized atomic clocks (TrueTime API) to commit writes locally while guaranteeing global serializability.
