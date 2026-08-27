# 09 — Microservices & API Design

## 🏛️ 1. Monolith vs Microservices: Architectural Evolution

```mermaid
flowchart TD
    subgraph Monolith["Monolithic Architecture"]
        M_UI["Web / Mobile UI"] --> M_App["Monolith (Auth + Orders + Payments + Inventory)"]
        M_App --> M_DB[("Single Shared Database")]
    end

    subgraph Microservices["Microservices Architecture"]
        MS_UI["Web / Mobile UI"] --> MS_GW["API Gateway"]
        MS_GW --> S1["Auth Service"] --> DB1[("Auth DB")]
        MS_GW --> S2["Order Service"] --> DB2[("Order DB")]
        MS_GW --> S3["Payment Service"] --> DB3[("Payment DB")]
        MS_GW --> S4["Inventory Service"] --> DB4[("Inventory DB")]
    end
```

| Dimension | Monolithic Architecture | Microservices Architecture |
|---|---|---|
| **Deployment** | Single unit deployment (all-or-nothing) | Independent deployment per service |
| **Database** | Shared database (risk of coupling queries) | **Database-per-Service** (strict encapsulation) |
| **Tech Stack** | Homogeneous (one language/framework) | Heterogeneous (polyglot: Go for high QPS, Python for AI, Java for billing) |
| **Blast Radius** | High (a memory leak in one module crashes whole app) | Low (isolated failure domain with circuit breakers) |
| **Operational Overhead**| Low (simple local debugging & logging) | High (requires distributed tracing, CI/CD pipelines, K8s orchestration) |

---

## 🔍 2. Service Discovery: Client-Side vs Server-Side

In dynamic cloud environments (Kubernetes, AWS ECS), service IP addresses change constantly as pods scale and restart.

```mermaid
flowchart TD
    subgraph ClientSideDiscovery["1. Client-Side Discovery (e.g. Eureka / Ribbon)"]
        C_Client["Service A"] -->|1. Query Active IPs| C_Registry["Service Registry (Consul / Eureka)"]
        C_Client -->|2. Direct RPC with local LB| C_Target["Service B (Pod 10.0.4.12)"]
    end

    subgraph ServerSideDiscovery["2. Server-Side Discovery (e.g. Kubernetes / AWS ALB)"]
        S_Client["Service A"] -->|1. Call 'service-b.internal'| S_LB["Load Balancer / K8s CoreDNS + Proxy"]
        S_LB -->|2. Reroutes to healthy Pod| S_Target["Service B (Pod 10.0.4.12)"]
    end
```

---

## ⚡ 3. The Circuit Breaker Pattern

Prevents cascading failures across a distributed microservices dependency graph.

```mermaid
stateDiagram-v2
    [*] --> Closed
    Closed --> Open : Failure Rate > 50% Threshold
    note right of Closed : Normal operation: Requests pass through.<br/>Counts successful & failed calls.
    
    Open --> HalfOpen : Sleep Window Elapsed (e.g., 30s)
    note right of Open : Fail-Fast: Immediately rejects calls with fallback response.<br/>Protects downstream service from overload.
    
    HalfOpen --> Closed : Trial Requests Succeed
    HalfOpen --> Open : Any Trial Request Fails
    note right of HalfOpen : Probes downstream with limited trial requests.
```

---

## 🔄 4. Distributed Transactions: 2PC vs The Saga Pattern

When each microservice has its own isolated database, ACID transactions across multiple databases are impossible without distributed coordination.

```mermaid
flowchart TD
    subgraph SagaChoreography["1. Choreography-Based Saga (Event-Driven)"]
        O1["Order Svc (Created)"] -->|Event: OrderCreated| P1["Payment Svc (Charges Card)"]
        P1 -->|Event: PaymentSuccess| I1["Inventory Svc (Reserves Stock)"]
        I1 -.->|On Out-of-Stock: PaymentFailed| P1Comp["Compensating Txn: Refund Card"]
    end

    subgraph SagaOrchestration["2. Orchestration-Based Saga (Central Coordinator)"]
        Orch["Saga Orchestrator<br/>(e.g. Temporal / AWS Step Functions)"]
        Orch -->|1. Execute| Svc1["Order Service"]
        Orch -->|2. Execute| Svc2["Payment Service"]
        Orch -->|3. Execute| Svc3["Inventory Service"]
        Orch -.->|On Failure: Compensate| Svc2Refund["Refund Payment"]
    end
```

| Pattern | Two-Phase Commit (2PC) | Choreography Saga | Orchestration Saga |
|---|---|---|---|
| **Coordination** | Centralized Transaction Manager | Decentralized (Kafka events) | Centralized state machine (Temporal) |
| **Consistency** | Strict ACID (Strong consistency) | Eventual Consistency | Eventual Consistency |
| **Blocking / Locking**| 🔴 Heavy database row locks held until commit | 🟢 Zero cross-service locks | 🟢 Zero cross-service locks |
| **Rollback** | Automatic database engine rollback | **Compensating Transactions** (Refund/Cancel) | **Compensating Transactions** (Automated) |
| **Best For** | Banking ledger within same database cluster | Simple 2–3 step asynchronous workflows | Complex 5+ step multi-service business workflows |
