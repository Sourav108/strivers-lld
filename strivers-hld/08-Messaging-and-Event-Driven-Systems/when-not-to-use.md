# When NOT to Use: Event Sourcing & Kafka

## ❌ When Event Sourcing & Kafka are the WRONG Decision:

1. **Simple CRUD Business Applications**:
   - *Why*: Event Sourcing requires maintaining event stores, writing projectors, rebuilding read models, and handling schema evolution across millions of immutable historical events.
   - *Better Choice*: Standard **State-based Relational CRUD** in PostgreSQL.
2. **Synchronous Request-Reply User Interactions**:
   - *Why*: Forcing an interactive HTTP login or password reset flow through Kafka queues introduces async polling latency and complex correlation ID tracking for zero benefit.
