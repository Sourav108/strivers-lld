# Staff-Level Requirements: Distributed Web Crawler

## 📋 The Staff Prompt
*"Design a distributed, highly polite, and fault-tolerant web crawler capable of downloading, parsing, deduplicating, and indexing 10 Billion web pages per month with adaptive freshness prioritization."*

---

## 🎯 Functional Requirements (FR)
1. **URL Frontier**: Prioritized and polite queue management.
2. **Parsing & Deduplication**: Fast HTML extraction, URL deduplication (Bloom Filter), and content near-duplicate detection (SimHash).
3. **Politeness & DNS Caching**: Strictly honor `robots.txt` and rate limits per target host.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Throughput**: 10 Billion pages/month $\approx \mathbf{4,000 \text{ pages/sec}}$.
2. **Politeness**: Zero concurrent requests to the same target domain within its defined delay window.
3. **Spider Trap Resilience**: Automated detection of infinite dynamic calendar loops.
