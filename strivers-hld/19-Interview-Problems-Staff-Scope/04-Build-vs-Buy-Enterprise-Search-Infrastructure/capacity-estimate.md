# Capacity Estimation: Enterprise Search Infrastructure

## 🔢 1. Traffic Estimates
- 500 Million searches/month $\approx \mathbf{200 \text{ search queries/sec}}$ (Peak: **2,000 QPS**).
- 50 Million Product Catalog Documents $\times 5\text{ KB per document} = \mathbf{250 \text{ GB raw data}}$.

---

## 💾 2. OpenSearch Cluster Sizing & Vector Storage
- 50M Products with 768-dimensional float32 vector embeddings = $50\text{M} \times 768 \times 4\text{ Bytes} \approx \mathbf{153 \text{ GB Vector RAM}}$.
- Total RAM for Inverted Index + HNSW Vector Graph $\approx \mathbf{500 \text{ GB RAM}}$.
- **Cluster Hardware**: 8 Nodes (AWS `r6g.2xlarge` with 64GB RAM and 500GB NVMe SSD each).
- **Monthly Cloud Cost**: $8 \times \$350/\text{month} \approx \mathbf{\$2,800/\text{month}}$ (compared to **\$350,000/month on Algolia** $\rightarrow$ **99% cost reduction**!).
