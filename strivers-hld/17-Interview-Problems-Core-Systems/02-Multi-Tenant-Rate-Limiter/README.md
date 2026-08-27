# High-Level Design: Multi-Tenant Distributed Rate Limiter

## 🏗️ 1. Architecture & Multi-Tenant Isolation

```mermaid
flowchart TD
    Client["Client Requests (Tenants)"] --> Envoy["Envoy API Gateway"]

    subgraph RateLimitingFilter["Rate Limiter Filter (Envoy RLS)"]
        LocalCache["Local Rule Cache (Caffeine)"]
        RL_Engine["Rate Limit Engine"]
    end

    Envoy --> RL_Engine
    RL_Engine <--> LocalCache

    subgraph MultiTenantRedisTier["Multi-Tenant Redis Cluster"]
        RedisShards["Redis Cluster (tenant_id shard)"]
        ConfigPostgres[("Rules DB (Postgres)")]
    end

    RL_Engine <-->|Sliding Window Lua (< 0.5ms)| RedisShards
    LocalCache -.->|CDC Sync| ConfigPostgres

    RL_Engine -->|200 OK| UpstreamCluster["Upstream Microservices"]
    RL_Engine -->|Throttled| 429["HTTP 429 Too Many Requests"]
```

---

## 🛡️ 2. Noisy Neighbor Protection: Sharding by `tenant_id`
- By hashing keys on `hash(tenant_id)`, all keys for Tenant A map to a dedicated Redis shard.
- If Tenant A sends 100,000 requests/sec, only their designated Redis shard experiences load, keeping Tenant B and C completely unaffected!
