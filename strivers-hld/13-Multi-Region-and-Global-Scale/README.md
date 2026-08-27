# 13 — Multi-Region & Global-Scale Architecture

## 🌍 1. Multi-Region Topologies: Active-Passive vs Active-Active

Deploying across multiple geographic regions is mandatory for 99.999% availability and low global latency.

```mermaid
flowchart TD
    subgraph ActivePassive["1. Active-Passive (Disaster Recovery)"]
        AP_DNS["GeoDNS"] -->|100% Writes| AP_Primary["Primary (US-East)"]
        AP_Primary -.->|Async Cross-Region DB Sync| AP_Standby["Standby (EU-West)"]
        AP_Primary -.->|Failover Alert| AP_Failover["Promote Standby<br/>(RTO < 5m)"]
    end

    subgraph ActiveActive["2. Active-Active (Global Multi-Master)"]
        AA_Anycast["Anycast Edge"] -->|EU Users| AA_EU["EU Datacenter (Active)"]
        AA_Anycast -->|US Users| AA_US["US Datacenter (Active)"]
        AA_EU <-->|Bidirectional Async Replication (CRDTs)| AA_US
    end
```

| Topology | Write Latency | Read Latency | Consistency | Cost & Operational Complexity |
|---|---|---|---|---|
| **Single Region** | 🚀 Fast local ($< 5\text{ms}$) | Slow for overseas users ($150\text{ms}$) | Strict ACID | 🟢 Lowest cost |
| **Active-Passive (Global Read Replicas)** | 🚀 Fast local | 🚀 Fast regional reads | Eventual consistency on reads | 🟡 Moderate cost |
| **Active-Active (Multi-Region Writes)** | 🚀 Fast local writes | 🚀 Fast local reads | Eventual / CRDT / TrueTime | 🔴 Highest cost & engineering complexity |

---

## ⚡ 2. Global Traffic Routing: Anycast vs GeoDNS

```mermaid
flowchart TD
    Routing["Global Ingress"]
    
    subgraph RoutingOptions["Routing Mechanisms"]
        Anycast["1. BGP Anycast<br/>(Same IP globally, instant failover)"]
        GeoDNS["2. GeoDNS (Route 53)<br/>(Resolves by client IP, DNS TTL delay)"]
    end

    Routing --> RoutingOptions
```

---

## 🔄 3. Cross-Region Conflict Resolution: CRDTs & Last-Write-Wins

When two users concurrently modify data in US and Europe before cross-region replication syncs:

1. **Last-Write-Wins (LWW)**: Relies on physical timestamps (risk of silent data loss on NTP clock drift).
2. **Conflict-Free Replicated Data Types (CRDTs)**:
   - Commutative and associative mathematical structures (e.g. PN-Counters, Observed-Removed Sets) where operations can be applied in any order across regions and still converge to the identical state without locks!
