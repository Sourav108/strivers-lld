# Capacity Estimation: Distributed Web Crawler @ 10B Pages

## 🔢 1. Throughput Estimates
- 10 Billion pages/month $\approx \mathbf{3,850 \text{ pages/sec}}$ (Peak: **8,000 pages/sec**).
- Average Page Size = **100 KB**.

---

## 💾 2. 5-Year Storage Estimates
- 10 Billion $\times 100 \text{ KB} = \mathbf{1 \text{ Petabyte (PB) / month}}$.
- 5-Year Storage = $\mathbf{60 \text{ Petabytes (PB)}}$ (Stored in distributed object stores / S3).

---

## ⚡ 3. Memory & Visited Frontier Sizing
- 50 Billion Discovered URLs $\times$ Bloom Filter ($1.8 \text{ Bytes/key}$) = $\mathbf{90 \text{ GB RAM}}$ (Easily fits across 3 Redis nodes).
- Ingress Bandwidth = $3,850 \times 100 \text{ KB} \approx \mathbf{385 \text{ MB/sec (3.08 Gbps)}}$.
