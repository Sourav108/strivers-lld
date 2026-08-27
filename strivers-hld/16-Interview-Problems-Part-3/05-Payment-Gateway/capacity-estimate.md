# Capacity Estimation: Payment Gateway System

## 🔢 1. Traffic & Payment QPS Estimates

- **Assumptions**:
  - Daily Transactions: **100 Million payments/day**
  - Read-to-Write Ratio: **1 : 1** (Payment write followed by webhook/status check)

### Payment QPS:
$$\text{Average Payment QPS} = \frac{100 \times 10^6}{10^5} = \mathbf{1,000 \text{ transactions/sec}}$$
$$\text{Peak Payment QPS (e.g. Cyber Monday)} = 1,000 \times 5 = \mathbf{5,000 \text{ transactions/sec}}$$

---

## 💾 2. Storage Estimation (10-Year Horizon)

- **Financial Transaction Record**:
  - `txn_id` (8 B), `merchant_id` (8 B), `amount` (8 B), `currency` (4 B), `status` (8 B), `card_token` (32 B), `created_at` (8 B) $\approx 200 \text{ Bytes/record}$.
  - Double-Entry Ledger entries ($2 \text{ lines per transaction} \times 150\text{ B} = 300 \text{ Bytes}$).
  - Total per transaction $\approx \mathbf{500 \text{ Bytes}}$.

- **10-Year Financial Storage Volume**:
$$\text{Total Storage} = 100\text{M/day} \times 365 \times 10 \times 500 \text{ Bytes} \approx \mathbf{182.5 \text{ Terabytes (TB)}}$$
*(Stored in sharded PostgreSQL / CockroachDB clusters with WORM immutable audit logs in Amazon S3 Object Lock).*
