# Trade-offs & TCO Matrix: Search Infrastructure

## ⚖️ 1. 3-Year Total Cost of Ownership (TCO) Comparison

| Strategy | Monthly Software / Cloud Bill | Annual Engineering SRE Overhead | 3-Year Cumulative TCO | Recommendation |
|---|---|---|---|---|
| **Hosted SaaS (Algolia)** | \$350,000 / mo | \$0 (Zero SRE needed) | **\$12.6 Million** | ❌ Unsustainable at current scale |
| **Managed Cloud (AWS OpenSearch)** | \$15,000 / mo | \$100,000 / yr (0.5 SRE) | **\$840,000** | 🟢 Good intermediate option |
| **Self-Hosted OpenSearch on EKS**| \$4,000 / mo | \$250,000 / yr (1 dedicated Staff SRE) | **\$894,000** | ✅ **Best for Custom ML Embeddings & Flexibility** |
