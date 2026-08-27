# When NOT to Use: Premature Database Sharding

## ❌ When Database Sharding is the WRONG Choice:

1. **Dataset Fits on a Single Read-Replica Topology ($< 2 \text{ TB}$)**:
   - *Why*: Sharding adds enormous architectural complexity: cross-shard transactions become impossible without distributed 2PC/Sagas, cross-shard JOINs require client-side aggregation, and schema migrations must be coordinated across dozens of separate database nodes.
2. **Uncertain Query Access Patterns**:
   - *Why*: Choosing the wrong Shard Key (e.g. sharding by `order_id` when 95% of queries filter by `user_id`) turns every query into an expensive **Scatter-Gather** query that hits all shards, degrading overall cluster throughput.
