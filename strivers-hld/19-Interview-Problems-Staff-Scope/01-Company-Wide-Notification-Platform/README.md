# High-Level Design: Company-Wide Notification Platform

## 🏗️ 1. Multi-Tenant Platform Architecture

```mermaid
flowchart TD
    subgraph InternalTenants["Internal Product Teams"]
        T1["Auth (OTP)"]
        T2["Billing (Invoices)"]
        T3["Growth (Marketing)"]
    end

    InternalTenants -->|Platform SDK| IngestGW["Platform Gateway"]

    subgraph GovernanceTier["Governance & Consent"]
        IngestGW --> QuotaEnforcer["Tenant Quotas (Redis)"]
        QuotaEnforcer --> ConsentStore["User GDPR Opt-Outs"]
        ConsentStore --> Deduplicator["Deduplicator"]
    end

    subgraph PriorityStreamingTier["Kafka Priority Topics"]
        Deduplicator --> P0["P0: Critical (OTP)"]
        Deduplicator --> P1["P1: Transactional"]
        Deduplicator --> P2["P2: Bulk Marketing"]
    end

    subgraph ChannelWorkers["Channel Workers & Failover"]
        P0 & P1 & P2 --> PushFleet["Push (APNS/FCM)"]
        P0 & P1 & P2 --> SMSFleet["SMS (Twilio/AWS)"]
        P0 & P1 & P2 --> EmailFleet["Email (SendGrid/SES)"]
    end

    PushFleet & SMSFleet & EmailFleet --> TelemetryDB[("Cost & Audit DB (ClickHouse)")]
```
