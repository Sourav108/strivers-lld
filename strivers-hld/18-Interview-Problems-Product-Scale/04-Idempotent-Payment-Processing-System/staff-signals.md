# Staff Signals: Idempotent Payment Processing System

## 🎯 Staff-Level Grading Criteria:
- **Idempotency State Transitions**: Details atomic state locking in Redis (`PENDING` $\rightarrow$ `SUCCESS`/`FAILED`) and TTL window design.
- **Double-Entry Accounting Invariant**: Enforces that balance is never an editable integer field, but an immutable ledger sum ($\sum \text{Credits} - \sum \text{Debits}$).
- **Automated Settlement Reconciliation**: Explains how 3-way reconciliation catches out-of-band refunds, chargebacks, and network dropouts via daily bank SFTP settlement parsing.
