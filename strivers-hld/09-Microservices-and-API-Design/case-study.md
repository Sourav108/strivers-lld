# Case Study: Microservice Decomposition & Resilience at Netflix & Uber

## 🏢 Context: Breaking the Monolith into 1,000+ Services

Both Netflix and Uber transitioned from a single monolithic repository to fleets of over 2,000 independent microservices.

```mermaid
flowchart TD
    Client["Client App"] --> Edge["Edge Zuul / Envoy Gateway"]
    Edge --> Aggregator["Backend for Frontend (BFF) Orchestrator"]
    Aggregator --> UserSvc["User Account Svc"]
    Aggregator --> RecSvc["Recommendation Engine (ML)"]
    Aggregator --> PlaybackSvc["Playback License Svc"]
    
    RecSvc -.->|Circuit Breaker Open (Slow / Timeout)| Fallback["Serve Static Top 10 Trending Titles"]
```

---

## 🛠 Engineering Innovations & Solutions

### 1. The Fallback Degradation Paradigm
- If Netflix's machine-learning recommendation service experiences a CPU spike or latency degradation, the **Hystrix / Resilience4j Circuit Breaker** trips instantly.
- Instead of returning a 500 error page to the user, the client is served a cached static list of "Popular on Netflix". The user never notices any outage!

### 2. Uber's Domain-Oriented Microservice Architecture (DOMA)
- **The Problem**: Managing 2,200 microservices caused cognitive overload, dependency spaghetti, and debugging nightmares.
- **The Solution**: Uber introduced **DOMA (Domain-Oriented Microservice Architecture)**:
  - Grouped thousands of microservices into logical **Domains** (e.g. Rides, Eats, Freight, Maps).
  - Each domain exposes a single **Gateway Service** with strict protobuf contracts. Internal domain microservices cannot be accessed directly by other domains.

### 3. Orchestrated Sagas with Cadence / Temporal
- Ride booking involves 8+ services: Driver matching, fare calculation, currency conversion, fraud check, payment authorization, and SMS notification.
- Uber uses **Cadence (Temporal)** to manage distributed sagas as fault-tolerant state machines with automatic compensation workflows on payment or driver failures.

---

## 📊 Key Architectural Takeaways

| Challenge | Monolith | Unregulated Microservices | DOMA / Managed Mesh |
|---|---|---|---|
| **Blast Radius** | Entire site goes down | Cascading failures across services | Isolated failure with automated fallbacks |
| **API Ownership** | Ambiguous code dependencies | Spaghetti network dependencies | Clear domain gateway contracts |
| **Transaction Rollback**| Native SQL `ROLLBACK` | Manual messy retry scripts | Deterministic Saga Orchestration |
