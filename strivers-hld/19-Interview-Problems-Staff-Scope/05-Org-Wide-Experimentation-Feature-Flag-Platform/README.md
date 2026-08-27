# High-Level Design: Org-Wide Experimentation Platform

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    subgraph ControlPlane["1. Experiment Management & Rule Distribution (Control Plane)"]
        Dashboard["Experimenter Dashboard (Data Scientists / PMs)"] --> AdminAPI["Experiment Management Service"]
        AdminAPI --> ConfigDB[("Experiment Metadata DB (PostgreSQL)")]
        AdminAPI --> CDN["Global CDN Edge (Rule Manifest: rules.json.gz)"]
    end

    subgraph DataPlane["2. Ultra-Low Latency Evaluation (Data Plane - In-Memory SDK)"]
        ClientApp["Mobile / Web / Microservice Host"] --> LocalSDK["Embedded Experimentation SDK"]
        CDN -.->|Poll every 60s or SSE Stream| LocalSDK
        LocalSDK -->|Murmur3 Hash (0.01ms in RAM)| Variant["Assigned Variant: Control vs Treatment A"]
    end

    subgraph MetricsPlane["3. Statistical Metrics & Exposure Pipeline (Analytics Plane)"]
        LocalSDK -->|Async Batched Exposure Event| Kafka["Kafka Exposure Stream"]
        Kafka --> Ingester["Stream Ingester (Flink)"]
        Ingester --> ClickHouse[("Real-Time Statistical OLAP (ClickHouse / Snowflake)")]
        ClickHouse --> Dashboard
    end
```

---

## 🧮 2. In-Memory Deterministic Variant Assignment Formula

$$\text{Bucket} = \text{Murmur3}(\text{user\_id} + \text{experiment\_id} + \text{salt}) \pmod{100}$$

- **Zero Network Latency**: Executed in CPU registers in **`< 10 microseconds`**.
- **Deterministic**: The same user consistently receives the same variant across all sessions without storing user-to-variant mappings in a centralized database!
