# When NOT to Use: NoSQL over RDBMS

## ❌ When NoSQL is the WRONG Choice:

1. **Complex Cross-Entity Relational JOINs & Aggregations**:
   - *Why*: In NoSQL databases (Cassandra/DynamoDB), performing multi-table joins requires client-side scatter-gather logic across thousands of network calls, degrading latency and developer velocity.
2. **Strict Financial Ledgers & Double-Entry Accounting**:
   - *Why*: Eventual consistency in NoSQL makes multi-row transactional balance invariants prone to race conditions and phantom balances.
   - *Better Choice*: **Relational Databases (PostgreSQL / MySQL)** with strict ACID transactions and serialized isolation levels.
