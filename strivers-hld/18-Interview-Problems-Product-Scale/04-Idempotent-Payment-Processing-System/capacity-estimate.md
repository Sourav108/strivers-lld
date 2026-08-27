# Capacity Estimation: Idempotent Payment Processing System

## 🔢 1. Throughput Estimates
- **Daily Payments**: **100 Million payments/day** $\approx \mathbf{1,000 \text{ txns/sec}}$ (Peak: **5,000 QPS**).

---

## 💾 2. 10-Year Storage Estimates
- Transaction Record + Double-Entry Ledger Lines $\approx 500 \text{ Bytes per txn}$.
- 10-Year Storage = $100\text{M/day} \times 365 \times 10 \times 500 \text{ Bytes} \approx \mathbf{182.5 \text{ Terabytes (TB)}}$ (CockroachDB / Sharded PostgreSQL with WORM audit S3 archives).
