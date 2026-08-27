# 14 — Operational Excellence & Failure Engineering

## 💥 1. The Reality of Distributed Failures

In hyperscale distributed systems, **hardware failure is not an anomaly; it is a statistical certainty every single hour**.

```mermaid
flowchart TD
    subgraph FailureModes["Failure Mitigation Arsenal"]
        direction TB
        FM1["1. Chaos Engineering<br/>(Kill production nodes)"]
        FM2["2. Backpressure & Load Shedding<br/>(Drop low-priority traffic)"]
        FM3["3. Graceful Degradation<br/>(Serve cached fallbacks)"]
        FM4["4. Blast-Radius Containment<br/>(Cell-based architecture)"]
    end
```

---

## 🌊 2. Backpressure & Load Shedding Algorithms

When downstream services (e.g. database or payment gateway) slow down, incoming requests queue up until thread pools exhaust and servers crash.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Client Traffic
    participant GW as API Gateway / Load Balancer
    participant Svc as Core Application Service
    participant DB as Overloaded Database

    Note over Svc: CPU > 85%, In-flight queue depth > 1000
    Client->>GW: Request A (Low-priority marketing)
    GW->>Svc: Forward Request A
    Svc-->>GW: HTTP 503 Service Unavailable (Dropped via Load Shedding in 0.1ms)
    GW-->>Client: HTTP 503 (Retry-After: 30s)
    
    Client->>GW: Request B (High-priority checkout payment)
    GW->>Svc: Forward Request B (Priority Tag: CRITICAL)
    Svc->>DB: Execute Payment
    DB-->>Svc: Payment Success
    Svc-->>Client: HTTP 200 OK
```

### Load Shedding Strategies:
1. **Priority Buckets**: Separate traffic into `CRITICAL` (checkout), `STANDARD` (browse), and `BACKGROUND` (analytics/crawlers). Drop background first.
2. **CoDel (Controlled Delay)**: Measure time requests spend waiting in queue before processing starts. If queue wait time exceeds 20ms, drop requests immediately without executing business logic.

---

## 🧪 3. Chaos Engineering Principles

- **Steady State Hypothesis**: Define normal metrics (e.g. 99.9% success rate, p99 latency < 100ms).
- **Vary Real-World Events**: Inject random network packet loss (10%), CPU spikes (100%), killed master database nodes, and severed cross-region links.
- **Automate Continuous Chaos**: Run chaos simulations automatically in production during working hours so on-call engineers are present.
