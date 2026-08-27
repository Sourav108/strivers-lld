# Capacity Estimation: Multi-Tenant Rate Limiter

## 🔢 1. Traffic Estimates
- **Global API Requests**: **500,000 requests/sec** across all company gateways (Peak: **1,000,000 QPS**).
- **Active Tenants**: 100,000 enterprise tenants.

---

## 💾 2. In-Memory Sizing
- Sliding Window Counter per tenant-route key $\approx 64 \text{ Bytes}$.
- Active concurrent keys in memory $\approx 10 \text{ Million keys}$.
$$\text{Memory} = 10 \times 10^6 \times 64 \text{ Bytes} \approx \mathbf{640 \text{ MB RAM}}$$
- Adding Redis cluster metadata & hashtable allocation $\approx \mathbf{3 \text{ GB RAM}}$ across 6 Redis shards.
