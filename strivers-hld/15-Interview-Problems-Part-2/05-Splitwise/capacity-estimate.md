# Capacity Estimation: Splitwise

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **10 Million users**
  - Daily Expenses Created: **10 Million expenses/day**
  - Daily Balance Checks: **50 Million reads/day**

### Expense Creation (Write) QPS:
$$\text{Write QPS} = \frac{10 \times 10^6}{10^5} = \mathbf{100 \text{ writes/sec}} \quad (\text{Peak: } 300 \text{ QPS})$$

### Balance Dashboard (Read) QPS:
$$\text{Read QPS} = \frac{50 \times 10^6}{10^5} = \mathbf{500 \text{ reads/sec}} \quad (\text{Peak: } 1,500 \text{ QPS})$$

---

## 💾 2. Storage Estimation (5-Year Horizon)

- **Expense Ledger Record**:
  - `expense_id` (8 B), `group_id` (8 B), `paid_by` (8 B), `amount` (8 B), `created_at` (8 B) $\approx 40 \text{ Bytes}$.
  - Split shares breakdown (average 4 users per bill $\times 20\text{ B} = 80 \text{ Bytes}$).
  - Total per expense record $\approx \mathbf{150 \text{ Bytes}}$.

- **5-Year Storage Volume**:
$$\text{Storage} = 10\text{M/day} \times 150 \text{ Bytes} \times 365 \times 5 \approx \mathbf{2.73 \text{ Terabytes (TB)}}$$
*(Can easily reside on a single PostgreSQL primary instance with read replicas).*
