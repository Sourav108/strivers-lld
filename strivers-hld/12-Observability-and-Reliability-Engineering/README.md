# 12 — Observability & Monitoring

## 🔭 1. The Three Pillars of Observability

In complex distributed architectures with hundreds of microservices, traditional single-server debugging is impossible. Observability provides insight into why a system is broken based on its external telemetry data.

```mermaid
flowchart TD
    Obs["Observability Core Pillars"]
    
    subgraph Pillars["Telemetry Types"]
        M["1. Metrics<br/>(Prometheus / Datadog)"]
        L["2. Logs<br/>(ELK / Loki / FluentBit)"]
        T["3. Distributed Traces<br/>(Jaeger / OpenTelemetry)"]
    end

    Obs --> Pillars
```

---

## 🧭 2. Distributed Tracing: Trace ID, Span ID & W3C Context

How do we follow a single user request across 20 distinct microservices?

```mermaid
sequenceDiagram
    autonumber
    actor User as User Request
    participant GW as API Gateway
    participant Auth as Auth Service
    participant Order as Order Service
    participant Pay as Payment Service

    Note over User,Pay: Trace ID = "4bf92f3577b34da6a3ce929d0e0e4736"
    User->>GW: HTTP GET /order/checkout
    Note over GW: Generates Span ID = "001"
    GW->>Auth: RPC VerifyToken (ParentSpanID="001", SpanID="002")
    Auth-->>GW: Token Valid (2ms)
    GW->>Order: RPC CreateOrder (ParentSpanID="001", SpanID="003")
    Order->>Pay: RPC Charge (ParentSpanID="003", SpanID="004")
    Pay-->>Order: Payment Approved (45ms)
    Order-->>GW: Order Created (52ms)
    GW-->>User: 200 OK (Total Trace Duration: 58ms)
```

- **Trace ID**: A globally unique identifier for the entire request path across all services.
- **Span ID**: Represents an individual unit of work executed by a single service (includes start time, end time, tags, and logs).
- **W3C Trace Context Header (`traceparent`)**: Standard HTTP header format: `version-trace_id-parent_id-trace_flags`.

---

## 🎯 3. SLA vs SLO vs SLI & Error Budgets

```mermaid
flowchart LR
    SLI["1. SLI (Indicator)<br/>'What did we measure?'<br/>e.g. 99.95% of /checkout calls < 200ms"] --> SLO["2. SLO (Internal Objective)<br/>'What is our engineering target?'<br/>e.g. 99.9% uptime per rolling 30 days"]
    SLO --> SLA["3. SLA (Legal Agreement)<br/>'What did we promise customers?'<br/>e.g. 99.5% uptime or pay financial penalty"]
```

### The Error Budget Concept
$$\text{Error Budget} = 100\% - \text{SLO}$$

- If an SLO is **99.9%** availability over 30 days, the team has an **Error Budget of 0.1%** ($\approx 43.8\text{ minutes}$ of acceptable downtime).
- **Rule of Thumb**:
  - If Error Budget is positive: Teams can ship new experimental features and deploy rapidly.
  - If Error Budget is exhausted: Feature freezes are enforced; 100% of engineering bandwidth switches to reliability and bug fixing.

---

## 🩺 4. Health Checks: Liveness vs Readiness Probes

In container orchestration platforms (Kubernetes), two distinct health probes prevent traffic from hitting broken or initializing pods:

```mermaid
flowchart TD
    subgraph Liveness["Liveness Probe (/healthz)"]
        L1["Checks: Is process deadlocked?"]
        L2["Action: K8s kills & restarts pod"]
    end

    subgraph Readiness["Readiness Probe (/ready)"]
        R1["Checks: Warmed caches & DB conns?"]
        R2["Action: LB removes from routing pool"]
    end
```
