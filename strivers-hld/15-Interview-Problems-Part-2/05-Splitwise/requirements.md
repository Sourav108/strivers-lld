# Requirements: Design Splitwise (Expense Sharing & Debt Simplification)

## 📋 Functional Requirements (FR)
1. **Add Expenses**: Users can create expenses within groups or 1-on-1, supporting equal splits, percentage splits, and exact share amounts.
2. **View Balances**: Users can view individual balances ("You owe \$50 to Alice", "Bob owes you \$20") and group total net balances.
3. **Debt Simplification Algorithm**: Minimize the total number of cash settlement transactions within a group using graph reduction.
4. **Settle Up**: Users can record payments to clear outstanding debts.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Strict Financial ACID Consistency**: Ledger balances must never experience race conditions or negative balance corruption.
2. **Low Read Latency**: Balance dashboard must load in **`< 50ms`**.
3. **High Availability**: 99.99% uptime for checking group balances and logging expenses.
4. **Scale**: Support **50 Million registered users** and **10 Million daily expense splits**.
