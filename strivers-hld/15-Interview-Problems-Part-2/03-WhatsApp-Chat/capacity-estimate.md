# Capacity Estimation: WhatsApp Chat System

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **1 Billion users**
  - Messages sent per user per day: **50 messages**
  - Total Daily Messages = $1\text{B} \times 50 = \mathbf{50 \text{ Billion messages/day}}$
  - Average message payload size: **100 Bytes** (Text + metadata)

### Message QPS:
$$\text{Average QPS} = \frac{50 \times 10^9}{10^5} = \mathbf{500,000 \text{ messages/sec}}$$
$$\text{Peak QPS} = 500,000 \times 2 = \mathbf{1,000,000 \text{ messages/sec}}$$

---

## 💾 2. Storage Estimation (Ephemeral vs Long-Term)

- **Daily Message Text Volume**:
$$\text{Daily Storage} = 50 \times 10^9 \times 100 \text{ Bytes} = \mathbf{5 \text{ Terabytes (TB)/day}}$$

- **WhatsApp Architecture Insight**:
  - Once a message is delivered to the recipient device, **WhatsApp deletes the message from its server disks**.
  - Only **undelivered offline messages** are stored in the server database (typically retained for max 30 days).
  - Max Offline Storage = $5 \text{ TB/day} \times 10\% \text{ offline rate} \times 30 \text{ days} \approx \mathbf{15 \text{ TB Storage}}$.

---

## ⚡ 3. WebSocket Connection Fleet Sizing
- **Peak Concurrent Connections**: **100 Million persistent TCP/WebSocket connections**.
- **Memory per Connection**:
  - With Linux Epoll & Erlang/Netty optimizations $\approx 10 \text{ KB per socket buffer}$.
  - Total RAM = $100 \times 10^6 \times 10 \text{ KB} = \mathbf{1 \text{ Terabyte RAM}}$.
  - A fleet of **50 Gateway Servers** (64GB RAM each) easily maintains 100M live persistent connections.
