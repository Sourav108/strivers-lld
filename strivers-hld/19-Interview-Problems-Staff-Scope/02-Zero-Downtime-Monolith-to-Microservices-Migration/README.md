# High-Level Design: Zero-Downtime Monolith to Microservices Migration

## 🏗️ 1. Strangler Fig & Dual-Write Architecture

```mermaid
flowchart TD
    Client["Client Checkout Traffic"] --> Gateway["API Gateway / Envoy Strangler Router"]

    subgraph LegacyMonolith["Legacy Monolithic Infrastructure"]
        MonolithApp["Monolithic Rails App"]
        MonolithDB[("Monolith DB (Postgres)")]
        MonolithApp --> MonolithDB
    end

    subgraph ModernCheckoutDomain["Modern Checkout Microservice"]
        NewOrderSvc["New Order Service (Go / Spring Boot)"]
        NewOrderDB[("New Order DB")]
        NewOrderSvc --> NewOrderDB
    end

    subgraph AsyncSyncTier["CDC & Reconciliation Pipeline"]
        DebeziumCDC["Debezium CDC (Postgres WAL)"]
        Kafka["Kafka Event Stream (Topic: db_cdc_orders)"]
        ReconWorker["Shadow Diff Worker"]
        
        MonolithDB --> DebeziumCDC --> Kafka --> ReconWorker
        NewOrderDB --> ReconWorker
    end

    Gateway -->|Phase 1: 100% Traffic| MonolithApp
    Gateway -.->|Phase 2: Dark Launch (Shadow Copy)| NewOrderSvc
    Gateway -->|Phase 3: 10% -> 50% -> 100% Live Cutover| NewOrderSvc
```

---

## 🔁 2. The 5-Step Zero-Downtime Data Migration Playbook

1. **Step 1: Deploy Strangler Router**: Place Envoy in front of all routes.
2. **Step 2: Build New Service & Schema**: Create clean microservice and isolated database.
3. **Step 3: Backfill Historical Data + CDC Replication**: Use Debezium to stream continuous WAL updates so the new DB catches up in real time.
4. **Step 4: Dark Launching & Diff Validation**: Route duplicate live write/read requests to the new service; compare responses in the background to achieve 99.999% parity over 14 days.
5. **Step 5: Cut Over & Sunset Monolith**: Shift primary traffic to the new microservice via dynamic feature flags; disable legacy code.
