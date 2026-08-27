# When NOT to Use: WebSockets & gRPC

## ❌ 1. When NOT to Use WebSockets:
- **Unidirectional Server Updates (e.g. Stock Tickers, Live Sports Scores, LLM Streaming)**:
  - *Why*: Maintaining 10 Million idle full-duplex TCP WebSockets requires heavy connection state and heartbeat ping-pongs.
  - *Better Choice*: **Server-Sent Events (SSE)**. Operates over standard HTTP/2, traverses corporate firewalls seamlessly, and provides built-in browser reconnection.
- **Infrequent Polling (< 1 update every 5 minutes)**:
  - *Why*: The memory overhead of keeping persistent TCP connections open exceeds the cost of simple HTTP GET requests.

---

## ❌ 2. When NOT to Use gRPC:
- **Public-Facing Browser APIs**:
  - *Why*: Web browsers do not natively support HTTP/2 trailing headers and raw binary Protocol Buffers without complex gRPC-Web proxies.
  - *Better Choice*: **REST / JSON** or **GraphQL** for public client-facing APIs; use **gRPC strictly for internal microservice-to-microservice RPCs**.
- **Third-Party Developer Integrations**:
  - *Why*: External API consumers expect readable JSON payloads and standard Postman/cURL debugging without compiling `.proto` files.
