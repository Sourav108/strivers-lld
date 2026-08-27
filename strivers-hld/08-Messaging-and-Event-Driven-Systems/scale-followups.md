# Scale Follow-ups: Messaging & Event Streaming at Scale

## 🚀 1. What Changes at 10x Scale?
- **Consumer Rebalance Storms**: When hundreds of consumer instances restart during deployment, Kafka triggers cluster-wide partition rebalances that pause all event consumption for minutes.
- **Solution**: Enable **Static Consumer Group Membership** (`group.instance.id`) and Cooperative Sticky Assignors to eliminate stop-the-world partition rebalances during rolling deployments.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Active-Active Cross-Region Event Replication**:
  - Replicating raw Kafka partitions synchronously across continents stalls producers with 150ms round-trips.
  - **Solution**: Use **MirrorMaker 2 / Brooklin** for asynchronous cross-cluster topic replication, namespacing topics by region (`us_east.orders`, `eu_west.orders`) to prevent cyclic infinite mirroring loops.
