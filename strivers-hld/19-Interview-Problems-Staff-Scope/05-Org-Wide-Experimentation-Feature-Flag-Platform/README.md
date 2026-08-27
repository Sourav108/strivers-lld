# High-Level Design: Org-Wide Experimentation Platform

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    subgraph ControlPlane["1. Control Plane (Rules Distribution)"]
        Dashboard["Experimenter UI"] --> AdminAPI["Admin API"]
        AdminAPI --> ConfigDB[("Metadata DB (Postgres)")]
        AdminAPI --> CDN["CDN Edge (Manifest)"]
    end

    subgraph DataPlane["2. Data Plane (In-Memory SDK)"]
        ClientApp["Client App / Pod"] --> LocalSDK["Embedded SDK"]
        CDN -.->|Poll 60s / SSE| LocalSDK
        LocalSDK -->|Murmur3 Hash (RAM)| Variant["Variant: Control / Treatment"]
    end

    subgraph MetricsPlane["3. Analytics Plane (Exposure Stream)"]
        LocalSDK -->|Async Batch Event| Kafka["Kafka Exposure Stream"]
        Kafka --> Ingester["Flink Ingester"]
        Ingester --> ClickHouse[("Analytics OLAP (ClickHouse)")]
        ClickHouse --> Dashboard
    end
```

---

## 🧮 2. In-Memory Deterministic Variant Assignment Formula

$$\text{Bucket} = \text{Murmur3}(\text{user\_id} + \text{experiment\_id} + \text{salt}) \pmod{100}$$

- **Zero Network Latency**: Executed in CPU registers in **`< 10 microseconds`**.
- **Deterministic**: The same user consistently receives the same variant across all sessions without storing user-to-variant mappings in a centralized database!
