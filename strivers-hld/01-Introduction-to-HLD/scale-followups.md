# Scale Follow-ups: Introduction to HLD & System Architecture

## 🚀 1. What Changes at 10x Scale (10M $\rightarrow$ 100M DAU)?
- **Database Bottleneck**: A single RDBMS with read replicas will saturate its master write throughput ($\approx 5,000 \text{ writes/sec}$). You must introduce horizontal database sharding or migrate write-heavy entities to an LSM-tree NoSQL database (e.g. ScyllaDB/Cassandra).
- **In-Memory Caching Pressure**: Redis single-threaded performance hits memory limits. You must transition to a partitioned Redis Cluster with client-side hashing and local L1 in-memory Caffeine caches on application hosts.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Network Latency & Speed of Light**: Cross-continental network RTT ($150\text{ms}$) becomes unacceptable for interactive user flows. You must deploy **Active-Active Multi-Region clusters** with regional traffic termination via BGP Anycast.
- **Cross-Region Replication Lag**: Synchronous cross-region database writes add hundreds of milliseconds of latency. You must relax consistency constraints to **Eventual Consistency** with conflict resolution (CRDTs or Last-Write-Wins with TrueTime).
- **Failure Domain Isolation**: An outage in one region (e.g. AWS `us-east-1`) must not cascade globally. The edge gateway must support automated regional traffic evacuation in $< 5\text{ minutes}$.
