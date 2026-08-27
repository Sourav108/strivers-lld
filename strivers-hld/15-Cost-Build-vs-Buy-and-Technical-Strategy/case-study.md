# Case Study: GitHub's Zero-Downtime MySQL Database Migration & Shopify's Modular Monolith

## 🏢 Context: Migrating Petabytes of Critical Data with Zero User Downtime

GitHub hosts hundreds of millions of repositories. Migrating their primary MySQL clusters across major database versions and data centers required zero read/write downtime.

```mermaid
sequenceDiagram
    autonumber
    actor Client as User / Web Tier
    participant Proxy as ProxySQL / Vitess Router
    participant OldDB as Old Primary MySQL (Source)
    participant NewDB as New Target MySQL (Replica)
    participant Freno as Freno Replication Throttler

    Client->>Proxy: Write Query
    Proxy->>OldDB: Execute Write
    OldDB->>NewDB: Async Binary Log Replication
    Freno->>NewDB: Monitor Replication Lag (< 1s)
    Note over Proxy: Step: Cutover Window (Hold writes in Proxy for < 100ms)
    Proxy->>OldDB: Set Read-Only
    Proxy->>NewDB: Promote New Target to Primary
    Proxy->>NewDB: Resume Queued Writes
    Note over Client: Zero errors observed by users!
```

---

## 🛠 Engineering Decisions & Takeaways

### 1. GitHub's `gh-ost` and Freno
- Standard `ALTER TABLE` locks giant tables for hours.
- GitHub built **`gh-ost`** (GitHub Online Schema Migrations) to apply migrations row-by-row in the background, pausing automatically if replication lag spikes via **Freno**.

### 2. Shopify's Modular Monolith vs Microservices
- Shopify deliberately rejected decomposing into hundreds of microservices.
- Instead, Shopify engineered a **Modular Monolith** using strict Ruby package encapsulation (`Packwerk`), saving tens of millions in cloud infrastructure costs and avoiding cross-service network latency.
