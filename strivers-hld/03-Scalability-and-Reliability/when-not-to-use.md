# When NOT to Use: Multi-Region Active-Active Architecture

## ❌ When Multi-Region Active-Active is the WRONG Decision:

1. **Strict Financial Transaction Consistency without TrueTime**:
   - *Why*: Operating an Active-Active multi-master SQL database across US and Asia causes cross-continental latency penalties ($150\text{ms}$ per distributed lock round) and high risk of write-conflict rollbacks.
   - *Better Choice*: **Active-Passive Multi-Region** (Single Primary region for writes with synchronous in-region replication and asynchronous cross-region read replicas).
2. **Cost & Operational Overhead Overkill**:
   - *Why*: Running identical 100% active fleets in 3 regions triples cloud infrastructure costs and complicates schema migrations. If the business can tolerate an **RTO of 15 minutes**, an automated Active-Passive failover pipeline is far more cost-effective.
