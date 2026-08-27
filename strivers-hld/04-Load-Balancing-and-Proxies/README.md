# 04 — Load Balancing & Proxies

## ⚖️ 1. Layer 4 (L4) vs Layer 7 (L7) Load Balancing

Load Balancers distribute incoming network traffic across multiple backend servers to prevent overload and ensure high availability.

```mermaid
flowchart TD
    Client["Client Traffic"] --> L4["L4 Load Balancer<br/>(Transport Layer / NLB)<br/>IP & Port Routing"]
    
    L4 --> L7["L7 Load Balancer<br/>(Application Layer / ALB)<br/>Path & Header Routing"]
    
    L7 -->|Path: /users/*| UserSvc["User Service"]
    L7 -->|Path: /orders/*| OrderSvc["Order Service"]
    L7 -->|Path: /payments/*| PaySvc["Payment Service"]
```

| Dimension | Layer 4 (L4) Load Balancing | Layer 7 (L7) Load Balancing |
|---|---|---|
| **OSI Layer** | Layer 4 (Transport Layer: TCP / UDP) | Layer 7 (Application Layer: HTTP / HTTPS / gRPC / WebSockets) |
| **Routing Basis** | Source/Dest IP Address & TCP/UDP Port | URL Path, Host header, HTTP Headers, Cookies, Payload |
| **TLS/SSL Handling** | Passes encrypted TCP packets directly (Pass-through) | Decrypts TLS (TLS Termination) & re-encrypts if needed |
| **Throughput & Speed** | 🚀 Extremely fast, minimal CPU overhead | Moderate CPU overhead (packet parsing & SSL crypto) |
| **Smart Routing** | ❌ No content awareness | ✅ Path-based, header-based, cookie-based routing |
| **Common Examples** | AWS NLB, HAProxy (TCP mode), Linux IPVS | AWS ALB, NGINX, Envoy, Traefik, Kong |

---

## 🧮 2. Load Balancing Algorithms

```mermaid
flowchart LR
    LB["Load Balancer"] --> Alg1["1. Round Robin"]
    LB --> Alg2["2. Weighted RR"]
    LB --> Alg3["3. Least Conns"]
    LB --> Alg4["4. Weighted Conns"]
    LB --> Alg5["5. IP Hash"]
    LB --> Alg6["6. Consistent Hash"]
```

1. **Round Robin**: Sequentially distributes requests to each server in order. (Best when all servers have identical hardware and requests have equal processing cost).
2. **Weighted Round Robin**: Assigns a weight to each server proportional to its hardware capacity (e.g., 8-core server gets 2x requests of a 4-core server).
3. **Least Connections**: Routes the request to the server with the fewest active TCP connections. (Best for long-lived sessions like WebSockets or heavy DB queries).
4. **Weighted Least Connections**: Considers both active connection count and server capacity weight.
5. **IP Hash (Source IP Hashing)**: Hashes the client IP to deterministically route requests from the same user to the same server (session sticky).
6. **Consistent Hashing**: Minimizes key re-mapping when backend nodes are added or removed (crucial for stateful caching clusters).

---

## 🔄 3. Forward Proxy vs Reverse Proxy

```mermaid
flowchart LR
    subgraph Forward["Forward Proxy (For Clients)"]
        C1["Client 1"] & C2["Client 2"] --> FP["Forward Proxy<br/>- Anonymity<br/>- URL Filter"]
        FP --> Internet1["Internet"]
    end

    subgraph Reverse["Reverse Proxy (For Servers)"]
        Internet2["Clients"] --> RP["Reverse Proxy<br/>- SSL & LB<br/>- DDoS Shield"]
        RP --> S1["Server 1"]
        RP --> S2["Server 2"]
    end
```

| Dimension | Forward Proxy | Reverse Proxy |
|---|---|---|
| **Acts on Behalf of** | The **Client** | The **Server** |
| **Position** | In front of internal client network | In front of backend server cluster |
| **Client Awareness** | Client explicitly configures proxy | Client is unaware (thinks proxy is the server) |
| **Primary Use Cases** | Bypassing firewalls, corporate URL filtering, client IP masking | Load balancing, SSL termination, caching, rate limiting, security |

---

## 🚪 4. The API Gateway Pattern

An **API Gateway** is a specialized Layer 7 reverse proxy that acts as the single entry point for all client traffic into a microservices architecture.

```mermaid
flowchart TD
    Client["Clients"] --> Gateway["API Gateway (Envoy / Kong)"]
    
    subgraph GatewayResponsibilities["Gateway Responsibilities"]
        direction TB
        G1["Auth & JWT Validation"]
        G2["Rate Limiting & Throttling"]
        G3["Dynamic Service Discovery"]
        G4["Request Aggregation (BFF)"]
        G5["Logging & Distributed Tracing"]
        G6["SSL Termination & CORS"]
    end

    Gateway --> GatewayResponsibilities
    GatewayResponsibilities --> S1["Auth Service"]
    GatewayResponsibilities --> S2["Product Service"]
    GatewayResponsibilities --> S3["Order Service"]
```

---

## ⚖️ Trade-offs: Single API Gateway vs Distributed Envoy Sidecars

| Pattern | API Gateway (Centralized) | Service Mesh / Sidecar (Envoy) |
|---|---|---|
| **Focus** | North-South traffic (Client to Services) | East-West traffic (Service to Service) |
| **Deployment** | Centralized cluster at edge | Sidecar process co-located with every container |
| **mTLS** | Edge-to-Gateway only | Zero-trust mutual TLS between every internal microservice |
| **Complexity** | Low / Moderate | High (requires control plane like Istio) |
