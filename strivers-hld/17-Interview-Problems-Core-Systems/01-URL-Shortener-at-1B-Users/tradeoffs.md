# Trade-offs & Deep Dive: URL Shortener @ 1B Users

## ⚖️ 1. Real-Time Analytics: Kafka + ClickHouse vs Direct DB Updates

| Dimension | Direct SQL Increment (`UPDATE urls SET clicks = clicks + 1`) | Async Kafka + ClickHouse Pipeline |
|---|---|---|
| **Write Lock Contention** | 🔴 Severe (Redirection path stalls on disk row locks) | 🟢 **Zero overhead on redirection path** |
| **Throughput** | Collapses at $> 2,000 \text{ QPS}$ | Scales to **1,000,000+ events/sec** |
| **Analytics Query Speed** | Slow range scans over billions of rows | Sub-second OLAP columnar queries in ClickHouse |
| **Decision** | ❌ Never update counts synchronously | ✅ **Kafka $\rightarrow$ ClickHouse Async Telemetry** |
