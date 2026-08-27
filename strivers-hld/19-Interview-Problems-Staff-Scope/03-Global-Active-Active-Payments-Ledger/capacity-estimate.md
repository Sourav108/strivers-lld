# Capacity Estimation: Global Active-Active Payments Ledger

## 🔢 1. Global Scale Estimates
- **Global Daily Payments**: **200 Million payments/day** $\approx \mathbf{2,000 \text{ txns/sec}}$ (Peak: **10,000 QPS**).
  - Americas (US-East/West): 50% (5,000 peak QPS)
  - Europe (Frankfurt/Ireland): 30% (3,000 peak QPS)
  - Asia-Pacific (Tokyo/Singapore): 20% (2,000 peak QPS)

---

## 💾 2. 10-Year Multi-Region Storage
- 200M/day $\times 365 \times 10 \times 600\text{ Bytes per ledger entry} = \mathbf{438 \text{ Terabytes (TB)}}$ replicated across 3 continents.
