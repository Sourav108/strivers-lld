# Capacity Estimation: Org-Wide Experimentation Platform

## 🔢 1. Evaluation & Exposure Traffic
- **Daily Active Users**: **100 Million DAU**.
- **Daily Exposure Events (Impressions)**: 100M users $\times$ 50 experiment exposures/day = $\mathbf{5 \text{ Billion exposures/day}}$.
- **Exposure Ingestion QPS**: $\frac{5 \times 10^9}{10^5} \approx \mathbf{50,000 \text{ events/sec}}$ (Peak: **150,000 QPS**).

---

## 💾 2. Rule Manifest Size & Local Memory
- 1,000 active experiments with targeting rules $\approx \mathbf{200 \text{ KB JSON manifest}}$.
- Evaluation RAM footprint per client/microservice pod = **`< 2 MB RAM`**.
- Egress Bandwidth for Rule Polling (CDN Cached with 60s TTL) = $\mathbf{10 \text{ MB/sec}}$.
