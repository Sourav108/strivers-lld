# Trade-offs & Deep Dive: WhatsApp Chat System

## ⚖️ 1. Epoll / Netty Event Loop vs Thread-Per-Connection

| Architecture | 1 Thread per Connection | Event-Driven Async I/O (Epoll / Netty / Erlang) |
|---|---|---|
| **Max Concurrent Conns** | 🔴 Crashes at $\sim 10,000$ threads (Stack memory limit) | 🟢 **1,000,000+ connections per server** |
| **Context Switching** | Severe CPU thrashing | Minimal CPU overhead |
| **Recommendation** | ❌ Deprecated | ✅ **Mandatory for Chat Gateways** |

---

## 👥 2. Scaling Group Chats (1,000 Members)

```mermaid
flowchart TD
    Sender["Sender in Group (1,000 members)"] --> GroupRouter["Group Message Router"]
    GroupRouter --> Kafka["Kafka Group Partition"]
    Kafka --> GroupWorker["Group Fanout Worker"]
    GroupWorker -->|Batch Query Active Sessions| RedisSession["Redis Session Store"]
    GroupWorker -->|Parallel Push to 50 Gateways| GWFleet["Gateway Cluster"]
```

- **Client-Side vs Server-Side Fanout**:
  - Sending 1,000 individual messages from the sender device drains battery and network bandwidth.
  - **Solution**: The sender uploads **1 message to the server**. The server-side Kafka worker fleet fans out the message to all 1,000 group participants in parallel.
