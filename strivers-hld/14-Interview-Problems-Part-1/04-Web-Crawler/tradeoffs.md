# Trade-offs & Deep Dive: Distributed Web Crawler

## ⚖️ 1. DFS vs BFS Crawling Strategy

| Strategy | Traversal Behavior | Risk / Benefit | Recommendation |
|---|---|---|---|
| **DFS (Depth-First Search)** | Traverses deeply down one site's link hierarchy | 🔴 Gets trapped in infinite recursive paths | ❌ Avoid for general web crawling |
| **BFS (Breadth-First Search)** | Explores immediate neighbor links first | 🟢 Discovers high-level authoritative pages evenly | ✅ **Industry Standard (with Priority weighting)** |

---

## 🚨 2. Spider Traps & Bot Mitigation

### 1. Infinite Spider Traps
- **Problem**: Websites generating endless dynamic calendar pages (`example.com/calendar?year=2026&month=9&day=...`).
- **Mitigations**:
  - Max URL length limit (e.g. 256 characters).
  - Max path depth threshold (e.g. max 10 subdirectory slashes `/a/b/c/...`).
  - Max page crawl quota per host domain per week.

### 2. DNS Resolution Bottleneck
- Standard OS DNS queries take 50ms–200ms, which stalls high-throughput crawler worker threads.
- **Solution**: Maintain a **local in-memory DNS Cache (Async C-ARES)**, pre-resolving domain IPs with 24-hour TTLs.
