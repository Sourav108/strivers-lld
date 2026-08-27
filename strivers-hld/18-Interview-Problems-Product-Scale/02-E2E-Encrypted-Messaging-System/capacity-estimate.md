# Capacity Estimation: E2E-Encrypted Messaging System

## 🔢 1. Traffic Estimates
- **Daily Messages**: **50 Billion messages/day** $\approx \mathbf{500,000 \text{ QPS}}$ (Peak: **1,000,000 QPS**).
- **Concurrent Live WebSockets**: **100 Million persistent connections**.

---

## 💾 2. Storage & Memory Sizing
- Average Encrypted Message Payload = 200 Bytes.
- Daily Volume = $50\text{B} \times 200\text{ B} = \mathbf{10 \text{ TB/day}}$.
- **Ephemeral Storage**: Retain only undelivered messages (10% offline rate $\times$ 30 days) = $\mathbf{30 \text{ TB}}$ (Stored in ScyllaDB / Cassandra with TTL 30d).
- **Socket RAM**: 100M connections $\times 10\text{ KB buffer} = \mathbf{1 \text{ Terabyte RAM}}$ across 50 Gateway nodes.
