# High-Level Design: Multi-Tenant Distributed Rate Limiter

## 🏗️ 1. Architecture & Multi-Tenant Isolation

```mermaid
flowchart TD
    Client["Client Requests (Tenants A, B, C)"] --> Envoy["Envoy API Gateway (Edge Reverse Proxy)"]

    subgraph RateLimitingFilter["Envoy Rate Limiter Filter (gRPC RLS)"]
        LocalCache["Local In-Process Caffeine Cache (Rule Configs)"]
        RL_Engine["Rate Limit Decision Engine"]
    end

    Envoy --> RL_Engine
    RL_Engine <--> LocalCache

    subgraph MultiTenantRedisTier["Multi-Tenant Redis Cluster"]
        RedisShards["Redis Cluster (Sharded by tenant_id)"]
        ConfigPostgres[("Dynamic Rules DB (PostgreSQL)")]
    end

    RL_Engine <-->|Atomic Sliding Window Lua (<0.5ms)| RedisShards
    LocalCache -.->|Change Data Capture (CDC)| ConfigPostgres

    RL_Engine -->|Allowed (200)| UpstreamCluster["Upstream Microservices"]
    RL_Engine -->|Throttled| 429["HTTP 429 Rate Limit Exceeded"]
```

---

## 🛡️ 2. Noisy Neighbor Protection: Sharding by `tenant_id`
- By hashing keys on `hash(tenant_id)`, all keys for Tenant A map to a dedicated Redis shard.
- If Tenant A sends 100,000 requests/sec, only their designated Redis shard experiences load, keeping Tenant B and C completely unaffected!
