# 01 — Introduction to High-Level Design (HLD)

## 📌 1. High-Level Design (HLD) vs Low-Level Design (LLD)

System design is broadly divided into two major architectural layers:

```mermaid
flowchart TD
    subgraph HLD["High-Level Design (Macro Architecture)"]
        A["DNS & CDN"] --> B["API Gateway / Load Balancer"]
        B --> C["Microservices Cluster"]
        C --> D["Distributed Cache (Redis)"]
        C --> E["Message Queue (Kafka)"]
        C --> F["Sharded DB (PostgreSQL / Cassandra)"]
    end
    
    subgraph LLD["Low-Level Design (Micro Architecture)"]
        G["Class Diagrams & OOP"]
        H["Design Patterns<br/>(Factory, Strategy, Observer)"]
        I["Concurrency & Threading<br/>(Locks, Semaphores)"]
        J["SOLID & Clean Code"]
    end
    
    HLD -.->|"Implemented By"| LLD
```

| Dimension | High-Level Design (HLD) | Low-Level Design (LLD) |
|---|---|---|
| **Focus** | Macro architecture, distributed components, data flow | Micro architecture, class models, algorithms, code structure |
| **Scope** | Servers, DBs, Caches, Queues, CDNs, Gateways | Classes, Interfaces, Methods, Design Patterns, Data Structures |
| **Questions Answered** | How do we scale to 10M DAU? How do we prevent SPOF? | How do we structure classes? Is the code extensible and maintainable? |
| **Core Concerns** | Scalability, High Availability, Fault Tolerance, Latency | Clean Code, SOLID Principles, Modularity, Thread Safety |
| **Deliverables** | Architecture diagrams, Capacity estimations, API specs, DB schemas | UML Class diagrams, Sequence diagrams, Working code implementations |

---

## 🌐 2. The Modern Client-Server Model

In modern distributed systems, the Client-Server model has evolved from a single monolithic server to an elastic, multi-tier distributed ecosystem:

```mermaid
sequenceDiagram
    autonumber
    actor Client as Mobile / Web Client
    participant DNS as Route53 / Cloudflare DNS
    participant CDN as Global Edge CDN
    participant LB as L7 Load Balancer
    participant Gateway as API Gateway (Auth & Rate Limit)
    participant Service as Application Service
    participant Cache as Redis Cache Cluster
    participant DB as Distributed Database

    Client->>DNS: Resolve api.example.com
    DNS-->>Client: Return nearest Anycast IP
    Client->>CDN: Request Static Asset / Video
    alt Cache Hit
        CDN-->>Client: 200 OK (Served from Edge)
    else Cache Miss
        CDN->>LB: Forward Dynamic API Request
        LB->>Gateway: Route Traffic (SSL Terminated)
        Gateway->>Gateway: Validate JWT & Rate Limit (Token Bucket)
        Gateway->>Service: Forward gRPC / HTTP request
        Service->>Cache: Read Cache (Cache-Aside)
        alt Cache Miss
            Service->>DB: Query Read Replica
            DB-->>Service: Return Record
            Service->>Cache: Populate Cache (TTL 1h)
        end
        Service-->>Gateway: Response Payload
        Gateway-->>Client: 200 OK (JSON)
    end
```

---

## 🎯 3. The 45-Minute System Design Interview Playbook

To ace any system design interview at top tech companies (FAANG / Tier-1), follow this time-tested 6-step framework:

```mermaid
gantt
    title 45-Minute System Design Interview Timeline
    dateFormat mm
    axisFormat %M min
    section Step 1
    Clarify Requirements & Scope :00, 05m
    section Step 2
    Back-of-the-Envelope Math    :05, 10m
    section Step 3
    API & Data Model Contract    :10, 15m
    section Step 4
    High-Level Architecture      :15, 30m
    section Step 5
    Deep Dives & Bottlenecks     :30, 40m
    section Step 6
    Trade-offs & Wrap-up         :40, 45m
```

### Step 1: Clarify Requirements & Scope (0–5 mins)
- **Functional Requirements (FR)**: What core actions must the user perform? (Keep to top 3–4 features).
- **Non-Functional Requirements (NFR)**:
  - Latency (e.g., `< 50ms` read latency, `< 200ms` write latency).
  - Availability vs Consistency (CAP trade-off: `99.99%` uptime vs strict ACID).
  - Scale (e.g., `100M DAU`, peak write spikes).
- **Out of Scope**: Explicitly clarify features you are intentionally not covering in 45 minutes.

### Step 2: Back-of-the-Envelope Math (5–10 mins)
- Calculate **Read QPS** and **Write QPS** (Average and Peak).
- Calculate **Storage requirements** over 5 years.
- Calculate **Memory (RAM) for Caching** (applying 80/20 Pareto rule).
- Calculate **Network Bandwidth** (Ingress & Egress).

### Step 3: API & Data Model Contract (10–15 mins)
- Define clean REST / gRPC endpoints with parameters and response payloads.
- Define relational or NoSQL database entities, primary keys, and index structures.

### Step 4: High-Level Architecture (15–30 mins)
- Draw the end-to-end data path: Client $\rightarrow$ CDN/DNS $\rightarrow$ Load Balancer $\rightarrow$ API Gateway $\rightarrow$ Microservices $\rightarrow$ Cache $\rightarrow$ DB.
- Explain the end-to-end read and write flows.

### Step 5: Deep Dives & Bottlenecks (30–40 mins)
- Dive into tricky problems: Partitioning/Sharding, Concurrency, Cache Invalidation, Race conditions, Hotkeys.
- Single Points of Failure (SPOF) and disaster recovery.

### Step 6: Trade-offs & Wrap-up (40–45 mins)
- Justify why chosen tech stack matches the NFRs (e.g., Cassandra vs Postgres, Kafka vs RabbitMQ).
- Identify future scaling optimizations.

---

## ⚡ 4. Core System Design Cheat Sheet

```
+-----------------------------------------------------------------------------------+
|  Requirement         | Recommended Solution Pattern                               |
+-----------------------------------------------------------------------------------+
| High Read QPS        | Redis/Memcached cache tier + Read Replicas + CDN           |
| High Write Throughput| Append-only logs, Kafka buffer, LSM-Tree NoSQL (Cassandra) |
| Low Latency Reads    | Geo-distributed CDN, Cache-Aside with In-memory Redis      |
| Financial Accuracy   | RDBMS with ACID transactions, 2PC/Saga, Idempotency keys   |
| Search Queries       | Elasticsearch / OpenSearch cluster with inverted index     |
| Real-time Updates    | WebSockets / SSE / Long Polling with Redis PubSub / Kafka  |
+-----------------------------------------------------------------------------------+
```
