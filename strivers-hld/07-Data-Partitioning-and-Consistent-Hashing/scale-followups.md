# Scale Follow-ups: Data Partitioning & Sharding at Scale

## 🚀 1. What Changes at 10x Scale?
- **Hot Partition Throttling**: A single merchant (e.g. Nike launching a sneaker sale) sends 50k writes/sec to a single partition key (`merchant_id:nike`), saturating the NVMe write throughput of that single database shard.
- **Solution**: Implement **Salted Keys**: append a random hash suffix to write keys (`nike:1`, `nike:2`, ..., `nike:10`) to distribute writes evenly across 10 shards, and merge results on read.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Cross-Region Resharding Operations**: Resharding a 500TB Cassandra cluster live across regions without locking tables.
- **Solution**: Use **Virtual Nodes (Vnodes)** and incremental background streaming with token range re-balancing.
