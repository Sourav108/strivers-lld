# When NOT to Use: Caching

## ❌ Scenarios Where Caching is an Anti-Pattern:

1. **Rapidly Mutating Data with Strict Consistency (e.g. Real-Time Trading Balances, Seat Booking)**:
   - *Why*: Invalidation race conditions between cache and database cause dirty reads and double booking. The overhead of invalidating cache on every write exceeds the benefit of reading from it.
2. **Low Cache Hit Ratios (< 70%)**:
   - *Why*: If query patterns are largely random (e.g. querying historical audit logs once a year), caching consumes expensive RAM while causing constant cache evictions with near-zero hit benefits.
3. **Using Redis as a Primary Source of Truth Database**:
   - *Why*: Redis is an in-memory datastore. While it supports RDB and AOF persistence, treating it as a relational master storage engine without write-ahead transactional guarantees risks data loss on cluster crash.
