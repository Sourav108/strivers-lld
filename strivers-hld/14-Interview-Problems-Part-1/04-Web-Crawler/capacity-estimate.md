# Capacity Estimation: Distributed Web Crawler

## 🔢 1. Throughput & Crawl QPS Estimates

- **Target**: **1 Billion web pages per month**
- **Average HTML Page Size**: **100 KB** (uncompressed text + metadata)

### Crawl QPS:
$$\text{Average Crawl QPS} = \frac{10^9}{30 \times 86,400} \approx \mathbf{385 \text{ pages/second}}$$
$$\text{Peak Crawl QPS} = 385 \times 2 \approx \mathbf{800 \text{ pages/second}}$$

---

## 💾 2. Storage Estimation (5-Year Horizon)

- **Monthly Raw Data**:
$$\text{Monthly Storage} = 10^9 \times 100 \text{ KB} = \mathbf{100 \text{ TB/month}}$$

- **5-Year Raw Content Storage**:
$$\text{5-Year Storage} = 100 \text{ TB} \times 60 \text{ months} = \mathbf{6 \text{ Petabytes (PB)}}$$
*(Stored in distributed object stores like Amazon S3 or HDFS).*

---

## ⚡ 3. Memory / Visited URLs Frontier Sizing

- To crawl 1 Billion pages, we discover roughly **10 Billion distinct URLs**.
- Average URL length = **100 Bytes**.
- If stored naively in RAM: $10 \times 10^9 \times 100 \text{ B} = \mathbf{1 \text{ TB RAM}}$.

### Space Optimization with Bloom Filters:
- By using a **Bloom Filter** with a 0.1% false positive rate (requiring 1.8 bytes / 14.4 bits per key):
$$\text{Bloom Filter RAM} = 10 \times 10^9 \times 1.8 \text{ Bytes} \approx \mathbf{18 \text{ GB RAM}}$$
*(Fits easily in the RAM of a single standard cloud node!)*

---

## 🌐 4. Network Bandwidth Requirements
$$\text{Ingress Bandwidth} = 385 \text{ pages/sec} \times 100 \text{ KB} \approx \mathbf{38.5 \text{ MB/second (308 Mbps)}}$$
*(Easily managed over standard 1Gbps / 10Gbps cloud network interfaces).*
