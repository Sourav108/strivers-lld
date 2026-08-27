# Capacity Estimation: URL Shortener

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - New URLs created: **100 Million new URLs per month**
  - Read-to-Write Ratio: **100 : 1** (10 Billion redirections per month)

### Write QPS:
$$\text{Writes per second} = \frac{100 \times 10^6}{30 \times 86,400} \approx \mathbf{40 \text{ URLs/second}}$$
$$\text{Peak Write QPS} = 40 \times 2 = \mathbf{80 \text{ URLs/second}}$$

### Read QPS (Redirection):
$$\text{Reads per second} = 40 \times 100 = \mathbf{4,000 \text{ queries/second (QPS)}}$$
$$\text{Peak Read QPS} = 4,000 \times 2 = \mathbf{8,000 \text{ QPS}}$$

---

## 💾 2. Storage Estimation (10-Year Horizon)

- **Record Schema Size**:
  - `short_key`: 7 bytes (VARCHAR)
  - `original_url`: 512 bytes (VARCHAR)
  - `user_id`: 8 bytes (BIGINT)
  - `created_at` & `expires_at`: 16 bytes (DATETIME)
  - Total per record $\approx \mathbf{600 \text{ Bytes}}$

### 10-Year Record Count:
$$\text{Total Records} = 100\text{M/month} \times 12 \times 10 = \mathbf{12 \text{ Billion URLs}}$$

### 10-Year Total Storage:
$$\text{Storage} = 12 \times 10^9 \times 600 \text{ Bytes} \approx \mathbf{7.2 \text{ TB}}$$
*(A single modern PostgreSQL / MongoDB sharded cluster can easily manage 7.2 TB).*

---

## ⚡ 3. Memory / Cache Estimation (80/20 Pareto Principle)

- Daily Redirections = $\frac{10 \times 10^9}{30} \approx \mathbf{330 \text{ Million reads/day}}$
- Daily Data Volume = $330\text{M} \times 600 \text{ Bytes} \approx \mathbf{200 \text{ GB/day}}$
- **Cache 20% of Daily Hot URLs in Redis**:
$$\text{Cache RAM} = 200 \text{ GB} \times 0.20 = \mathbf{40 \text{ GB RAM}}$$
*(Can easily fit inside a single 64GB Redis AWS `r6g.xlarge` instance).*

---

## 🌐 4. Bandwidth Estimates
- **Ingress (Write)**: $40 \text{ req/s} \times 600 \text{ Bytes} \approx \mathbf{24 \text{ KB/s}}$
- **Egress (Read)**: $4,000 \text{ req/s} \times 600 \text{ Bytes} \approx \mathbf{2.4 \text{ MB/s}}$
