# High-Level Design: Global Active-Active Payments Ledger

## 🏗️ 1. Multi-Region Active-Active Architecture

```mermaid
flowchart TD
    subgraph US_Region["Americas Region (Active)"]
        US_GW["US Edge Gateway"] --> US_Ledger["US Ledger Service"]
        US_Ledger --> US_Spanner["Google Spanner / CockroachDB (US Multi-AZ Range)"]
    end

    subgraph EU_Region["Europe Region (Active)"]
        EU_GW["EU Edge Gateway"] --> EU_Ledger["EU Ledger Service"]
        EU_Ledger --> EU_Spanner["CockroachDB (EU Multi-AZ Range - GDPR Compliant)"]
    end

    subgraph APAC_Region["Asia-Pacific Region (Active)"]
        APAC_GW["APAC Edge Gateway"] --> APAC_Ledger["APAC Ledger Service"]
        APAC_Ledger --> APAC_Spanner["CockroachDB (APAC Multi-AZ Range)"]
    end

    US_Spanner <-->|Asynchronous Global Inter-Region Sync| EU_Spanner
    EU_Spanner <-->|Asynchronous Global Inter-Region Sync| APAC_Spanner
    APAC_Spanner <-->|Asynchronous Global Inter-Region Sync| US_Spanner
```

---

## ⚡ 2. The Locality-Aware Account Range Strategy
- By partitioning ledger account rows using **Locality Constraints** (`account_country = 'DE' -> store in EU-Frankfurt cluster`):
  - Local domestic payments commit with strict ACID serializability within local datacenter AZs in **`< 10ms`**.
  - No cross-continental network latency for 98% of world payments!
