# Staff Signals: Multi-Tenant Distributed Rate Limiter

## 🎯 Staff-Level Grading Criteria:
- **Noisy Neighbor Awareness**: Demonstrates concrete tenant sharding and CPU quota isolation.
- **Fail-Open Strategy**: Clear articulation of why availability trumps strict rate precision during cache outages.
- **Lua Script Race Condition Mitigation**: Proves how in-memory Lua executes atomically without cross-service locking.
- **Dynamic Config Invalidation**: Uses CDC / Kafka to push rule updates to gateway local caches in $< 100\text{ms}$.
