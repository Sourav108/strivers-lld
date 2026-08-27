# Scale Follow-ups: Microservices & API Design

## 🚀 1. What Changes at 10x Scale?
- **Saga Orchestration Bottlenecks**: As transaction volume grows to 5,000 sagas/sec, a centralized orchestrator database becomes a bottleneck.
- **Solution**: Transition to **Choreography-based Sagas** for high-throughput, low-step event pipelines, reserving stateful Temporal/Cadence orchestration for high-value complex financial transactions.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Enterprise-Wide Deprecation Strategies**: When modifying an API consumed by 50 internal engineering teams, breaking changes cause outages.
- **Solution**: Implement **Multi-Version Protocol Buffers**: maintain backwards compatibility by adding new field tags, supporting shadow/dark traffic verification, and tracking consumer telemetry headers before sunsetting legacy endpoints.
