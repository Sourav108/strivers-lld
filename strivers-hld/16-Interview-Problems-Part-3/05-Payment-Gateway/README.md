# High-Level Design: Payment Gateway System

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Merchant["Merchant App / Website"] --> Gateway["Payment Gateway Edge (TLS Termination)"]

    subgraph SecurityAndIdempotency["Security & Idempotency Layer"]
        Gateway --> TokenVault["PCI-DSS Tokenization Vault (HSM)"]
        Gateway --> IdempStore["Idempotency Store (Redis Cluster)"]
    end

    subgraph CorePaymentEngine["Payment Orchestration Engine (Saga / Temporal)"]
        Gateway --> PaymentSvc["Payment Authorization Service"]
        PaymentSvc --> RiskEngine["Anti-Fraud & Risk Assessment ML"]
        PaymentSvc --> BankRouter["Smart Bank Routing Engine"]
    end

    subgraph ExternalFinancialNetworks["External Banking Rails"]
        BankRouter --> Visa["Visa / Mastercard Network"]
        BankRouter --> AcquiringBank["Acquiring Bank Processor"]
    end

    subgraph AccountingLedger["Ledger & Reconciliation Tier"]
        PaymentSvc --> LedgerSvc["Double-Entry Ledger Service"]
        LedgerSvc --> LedgerDB[("PostgreSQL Ledger DB (ACID Strict)")]
        ReconciliationWorker["Daily Settlement Reconciliation Worker"]
        ReconciliationWorker <--> LedgerDB
        ReconciliationWorker <--> BankReportFiles["Bank SFTP Settlement Files"]
    end
```

---

## 🔒 2. Distributed Idempotency State Machine

```mermaid
sequenceDiagram
    autonumber
    actor Merchant as Merchant System
    participant PG as Payment Gateway
    participant Redis as Redis Idempotency Lock
    participant Bank as Acquiring Bank API
    participant DB as Postgres Transaction DB

    Merchant->>PG: POST /v1/charges (Idempotency-Key: "idemp_abc_789", amount=$100)
    PG->>Redis: SET "idemp:idemp_abc_789" "PENDING" NX EX 120
    alt Key already exists with COMPLETED status
        PG-->>Merchant: Return previously cached response (200 OK)
    else First Time Request
        PG->>DB: Insert txn record (Status: "PENDING")
        PG->>Bank: Process Charge Request (Card Network)
        Bank-->>PG: Charge Authorized (Status: "SUCCESS")
        PG->>DB: Update txn record (Status: "SUCCESS")
        PG->>Redis: SET "idemp:idemp_abc_789" "{status: 'SUCCESS', charge_id: 'ch_99'}" EX 86400
        PG-->>Merchant: 200 OK (Charge Succeeded)
    end
```

---

## 🏛️ 3. Double-Entry Accounting Ledger

In financial accounting, money is never created or destroyed out of thin air; every debit has a matching credit ($\sum \text{Debits} = \sum \text{Credits}$):

```mermaid
flowchart LR
    CustomerCard["Customer Available Balance (-$100) [Debit]"] --> MerchantAcct["Merchant Settlement Account (+$97) [Credit]"]
    CustomerCard --> GatewayFee["Payment Gateway Fee Account (+$3) [Credit]"]
```

```sql
CREATE TABLE ledger_entries (
    entry_id BIGINT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(18, 4) NOT NULL, -- Positive for Credit, Negative for Debit
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_txn (transaction_id),
    INDEX idx_account (account_id)
);
```
