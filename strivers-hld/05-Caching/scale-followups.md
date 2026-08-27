# Scale Follow-ups: Caching at Ultra Scale

## 🚀 1. What Changes at 10x Scale?
- **Hot-Key CPU Starvation**: When a viral post or breaking news item hits 100k requests/sec, the single Redis shard hosting that key pegs at 100% CPU while other shards sit idle.
- **Solution**:
  - Implement a **Two-Tier Caching Strategy**: L1 in-process RAM cache (Caffeine with 5-second TTL on application pods) + L2 Redis Cluster.
  - Apply **Key Splitting**: Append random replica suffixes to hot keys (`hot_key:1`, `hot_key:2`, ..., `hot_key:16`) and balance client reads across shards.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Cross-Region Cache Invalidation**: When a database write commits in US-East, how do you invalidate the cache in EU-West without stale data?
- **Solution**: Use **CDC (Change Data Capture)** via Debezium/Kafka to stream database write-ahead logs to global Kafka topics, triggering regional worker pods to invalidate local Redis keys within 100ms.
