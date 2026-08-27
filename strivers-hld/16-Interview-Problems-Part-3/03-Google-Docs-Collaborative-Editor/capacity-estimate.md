# Capacity Estimation: Google Docs Collaborative Editor

## 🔢 1. Traffic & Keystroke QPS Estimates

- **Assumptions**:
  - Daily Active Users (DAU): **100 Million**
  - Peak Concurrent Active Editors: **10 Million users**
  - Average Keystroke Rate per Active Typist: **2 keystrokes/second** (with client-side 100ms debouncing / operation batching)

### Keystroke / Operation QPS:
$$\text{Average Ingestion QPS} = 10 \times 10^6 \times 2 = \mathbf{20,000,000 \text{ operations/sec}}$$

---

## 💾 2. Storage Estimation (10-Year Horizon)

- **Assumptions**:
  - 1 Billion documents total.
  - Average document size: **100 KB text**.
  - Document changelog history: **500 KB operations**.
  - Total per document $\approx \mathbf{600 \text{ KB}}$.

$$\text{Total Storage} = 10^9 \times 600 \text{ KB} = \mathbf{600 \text{ Terabytes (TB)}}$$
*(Stored in distributed document stores like Bigtable / MongoDB with cold snapshot offloading to S3).*
