# Trade-offs & Deep Dive: Distributed Web Crawler

## ⚖️ 1. Exact URL Set vs Probabilistic Bloom Filter

| Dimension | Exact String Set (Redis Hash Set) | Probabilistic Bloom Filter |
|---|---|---|
| **Memory Cost** | 50B URLs $\times 100\text{ B} = \mathbf{5 \text{ Terabytes RAM}}$ | 50B URLs $\times 1.8\text{ B} = \mathbf{90 \text{ GB RAM}}$ |
| **False Negative Rate**| 0% | **0% (Zero False Negatives guaranteed)** |
| **False Positive Rate**| 0% | 0.1% (Drops 1 in 1,000 unvisited URLs) |
| **Decision** | ❌ Prohibitively expensive | ✅ **Massive 98% RAM Savings** |
