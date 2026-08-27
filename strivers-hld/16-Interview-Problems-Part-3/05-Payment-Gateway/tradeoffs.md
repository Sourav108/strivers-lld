# Trade-offs & Deep Dive: Payment Gateway System

## ⚖️ 1. Synchronous vs Asynchronous Payment Settlement

| Model | User Experience | Resilience | Recommendation |
|---|---|---|---|
| **Synchronous Authorization** | Customer receives immediate feedback (Success/Failure) in $< 2\text{s}$. | Vulnerable if banking network slows down. | **Mandatory for Card Authorization** |
| **Asynchronous Clearing & Settlement** | Bank funds transferred 24–48 hours later via batch ACH/SEPA files. | High resilience (Handled via background batch reconciliations). | **Standard Financial Settlement Rail** |

---

## 🔍 2. Daily Settlement Reconciliation Engine

- Banks generate daily **SFTP Settlement Files (MT940 / CAMT.053 / CSV)** containing every settled transaction from the previous banking day.
- A scheduled night job executes a **Three-Way Match**:
  1. Internal Payment Gateway Database.
  2. Internal Double-Entry Ledger Entries.
  3. External Bank Settlement Statements.
- Discrepancies (e.g. uncaptured charges, chargebacks) are automatically flagged to an operations dashboard for human review.
