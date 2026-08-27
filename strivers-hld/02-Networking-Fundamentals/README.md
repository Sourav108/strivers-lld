# 02 — Networking Fundamentals for System Design

## 🌐 1. OSI vs TCP/IP Protocol Stack

Distributed systems rely on standard network models to transmit data reliably across the globe.

```mermaid
flowchart LR
    subgraph OSI["OSI 7-Layer Model"]
        direction TB
        O7["7. Application (HTTP, DNS, gRPC)"]
        O6["6. Presentation (SSL/TLS, JSON)"]
        O5["5. Session (Sockets, RPC sessions)"]
        O4["4. Transport (TCP, UDP, QUIC)"]
        O3["3. Network (IP, ICMP, BGP)"]
        O2["2. Data Link (Ethernet, Wi-Fi, MAC)"]
        O1["1. Physical (Fiber optic, Cables)"]
    end

    subgraph TCPIP["TCP/IP 4-Layer Model"]
        direction TB
        T4["Application Layer<br/>(HTTP/HTTPS, DNS, SSH, gRPC)"]
        T3["Transport Layer<br/>(TCP, UDP, QUIC)"]
        T2["Internet Layer<br/>(IPv4, IPv6, ICMP, Routing)"]
        T1["Network Interface / Link Layer<br/>(Ethernet, Wi-Fi, ARP)"]
    end

    O7 & O6 & O5 --> T4
    O4 --> T3
    O3 --> T2
    O2 & O1 --> T1
```

---

## 🔍 2. DNS (Domain Name System) Resolution Flow

When a client queries `https://api.example.com`, how does it resolve the IP address in `< 20ms`?

```mermaid
sequenceDiagram
    autonumber
    actor Client as Browser / Client App
    participant LocalDNS as Recursive Resolver (ISP / 8.8.8.8)
    participant RootDNS as Root DNS Server (.)
    participant TLDDNS as TLD DNS Server (.com)
    participant AuthDNS as Authoritative DNS (Cloudflare / Route53)

    Client->>LocalDNS: Query "api.example.com" (A record)
    alt In Local DNS Cache
        LocalDNS-->>Client: Return IP (e.g., 104.18.25.1)
    else Cache Miss
        LocalDNS->>RootDNS: Where is .com?
        RootDNS-->>LocalDNS: Refer to .com TLD Server IP
        LocalDNS->>TLDDNS: Where is example.com?
        TLDDNS-->>LocalDNS: Refer to Authoritative Nameserver (ns1.example.com)
        LocalDNS->>AuthDNS: What is IP of api.example.com?
        AuthDNS-->>LocalDNS: Returns IP (104.18.25.1, TTL=300s)
        LocalDNS->>LocalDNS: Cache response for 300s
        LocalDNS-->>Client: Returns IP (104.18.25.1)
    end
```

### DNS Routing Techniques in System Design:
1. **GeoDNS**: Routes user to the nearest regional data center based on client IP geolocation.
2. **Anycast DNS**: Same IP address announced from hundreds of locations worldwide; BGP automatically routes to the topologically closest node.
3. **Round-Robin DNS**: Cycles through multiple IP addresses for basic client-side load balancing.

---

## 🚀 3. HTTP Evolution: HTTP/1.1 vs HTTP/2 vs HTTP/3 (QUIC)

```mermaid
flowchart TD
    subgraph H1["HTTP/1.1 (1997)"]
        H1A["1 Req per TCP Connection<br/>Head-of-Line Blocking<br/>Uncompressed Headers"]
    end
    
    subgraph H2["HTTP/2 (2015)"]
        H2A["Binary Multiplexing<br/>Single TCP Connection<br/>HPACK Header Compression<br/>TCP HOL Blocking on Loss"]
    end

    subgraph H3["HTTP/3 (QUIC - 2022)"]
        H3A["Runs over UDP (QUIC)<br/>0-RTT TLS 1.3 Handshake<br/>Zero Stream HOL Blocking<br/>Connection IP Migration"]
    end
```

| Feature | HTTP/1.1 | HTTP/2 | HTTP/3 (QUIC) |
|---|---|---|---|
| **Underlying Transport** | TCP | TCP | UDP (via QUIC) |
| **Multiplexing** | ❌ (Needs multiple TCP conns) | ✅ (Streams on single TCP conn) | ✅ (Independent UDP streams) |
| **Head-of-Line Blocking** | 🚨 Application & Transport layer | 🚨 Transport layer (Packet loss stalls all streams) | 🟢 Zero HOL Blocking across streams |
| **Connection Handshake** | TCP (1 RTT) + TLS (1-2 RTT) = 2-3 RTT | TCP + TLS 1.3 = 1-2 RTT | QUIC + TLS 1.3 = 0-1 RTT (Instant connect) |
| **Header Compression** | ❌ None | ✅ HPACK | ✅ QPACK |
| **Network Migration** | ❌ Connection dropped on IP change | ❌ Connection dropped | ✅ Connection ID persists across IP change |

---

## ⚡ 4. Real-Time Communication Protocols

How do we push real-time updates (chat, live notifications, market tickers)?

```mermaid
flowchart TD
    Client["Client App"] --> Poll["Short Polling<br/>(Periodic GET requests)"]
    Client --> LongPoll["Long Polling<br/>(Hangs until new event)"]
    Client --> SSE["Server-Sent Events<br/>(Unidirectional Stream)"]
    Client --> WS["WebSockets<br/>(Bidirectional TCP Socket)"]
```

| Protocol | Direction | Overhead | Reconnection | Best Use Case |
|---|---|---|---|---|
| **Short Polling** | Client $\rightarrow$ Server | 🔴 Extremely High (constant HTTP headers) | Natural | Low-frequency status checks (e.g. build status) |
| **Long Polling** | Client $\rightarrow$ Server (delayed) | 🟡 Moderate (re-establishes HTTP connection per message) | Handled by client loop | Legacy chat fallback, notification polling |
| **Server-Sent Events (SSE)** | Server $\rightarrow$ Client only | 🟢 Low (Single HTTP connection, built-in retry) | Automatic | Live scoreboards, stock tickers, LLM streaming output |
| **WebSockets** | Client $\leftrightarrow$ Server (Bidirectional) | 🟢 Lowest (Single upgraded TCP connection, lightweight frames) | Manual handling | Online multiplayer gaming, collaborative editing, chat apps |

---

## 🔄 5. API Paradigms: REST vs GraphQL vs gRPC vs Thrift

```mermaid
flowchart LR
    A["API Paradigms"] --> B["REST<br/>(JSON / HTTP 1.1 & 2)"]
    A --> C["GraphQL<br/>(Flexible Client Queries)"]
    A --> D["gRPC<br/>(Protobuf / High Throughput)"]
```

| Dimension | REST | GraphQL | gRPC |
|---|---|---|---|
| **Protocol & Format** | HTTP 1.1/2, JSON/XML | HTTP POST, JSON | HTTP/2 / QUIC, Protocol Buffers (Binary) |
| **Data Fetching** | Over-fetching or Under-fetching common | Exact fields requested (No over/under-fetching) | Strict schema defined in `.proto` |
| **Performance / Latency** | Moderate (Text JSON parsing overhead) | Moderate (Server query resolution overhead) | 🚀 Ultra-fast (Binary serialization is 5-10x faster) |
| **Streaming Support** | Limited (Chunked transfer) | Subscriptions (over WebSockets) | Full support: Unary, Client, Server, Bidirectional streaming |
| **Best For** | Public APIs, CRUD web services | Mobile apps with complex relational UI views | Microservice-to-microservice internal RPC communication |
