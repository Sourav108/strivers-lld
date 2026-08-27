# Trade-offs & Deep Dive: Global Payments Ledger

## ⚖️ 1. Global Synchronous Quorums vs Home-Region Localized Quorums

| Dimension | Global Synchronous Quorums | Home-Region Localized Quorums (CockroachDB / Spanner) |
|---|---|---|
| **Domestic Payment Latency** | 🔴 180ms (Cross-continental network RTT per write) | 🚀 **< 10ms (Local Multi-AZ commit)** |
| **Cross-Border Transfer Latency**| 180ms | 180ms (Two-Phase Commit across regions) |
| **Decision** | ❌ Destroys local merchant checkout conversion | ✅ **Gold Standard for Global Fintech** |
