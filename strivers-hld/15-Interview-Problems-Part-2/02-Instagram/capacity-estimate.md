# Capacity Estimation: Instagram

## 🔢 1. Traffic & QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **500 Million**
  - Daily Photos Uploaded: **100 Million photos/day**
  - Daily Photo Views = $500\text{M} \times 20 = \mathbf{10 \text{ Billion views/day}}$
  - Average Compressed Photo Size: **2 MB**

### Upload (Write) QPS:
$$\text{Write QPS} = \frac{100 \times 10^6}{10^5} = \mathbf{1,000 \text{ uploads/sec}} \quad (\text{Peak: } 2,000 \text{ QPS})$$

### View (Read) QPS:
$$\text{Read QPS} = \frac{10 \times 10^9}{10^5} = \mathbf{100,000 \text{ photo reads/sec}} \quad (\text{Peak: } 200,000 \text{ QPS})$$

---

## 💾 2. Storage Estimation (5-Year Horizon)

- **Daily Media Storage Ingestion**:
$$\text{Daily Storage} = 100 \times 10^6 \times 2 \text{ MB} = \mathbf{200 \text{ Terabytes (TB)/day}}$$

- **5-Year Media Storage**:
$$\text{5-Year Storage} = 200 \text{ TB} \times 365 \times 5 \approx \mathbf{365 \text{ Petabytes (PB)}}$$
*(Stored in Amazon S3 / Google Cloud Storage with intelligent Glacier tiering).*

---

## 🌐 3. Bandwidth Estimates
- **Ingress (Upload)**: $1,000 \text{ req/s} \times 2 \text{ MB} \approx \mathbf{2 \text{ GB/second (16 Gbps)}}$
- **Egress (CDN Delivery)**: $100,000 \text{ req/s} \times 2 \text{ MB} \approx \mathbf{200 \text{ GB/second (1.6 Tbps)}}$
*(98% of egress traffic is absorbed directly by distributed Edge CDNs).*
