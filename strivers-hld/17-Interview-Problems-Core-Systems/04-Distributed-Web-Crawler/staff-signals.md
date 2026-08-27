# Staff Signals: Distributed Web Crawler

## 🎯 Staff-Level Grading Criteria:
- **Politeness Implementation**: Explains the dual-tier queue architecture (Priority + Host Politeness) without hand-waving.
- **DNS & Connection Reuse**: Recognizes that DNS lookups are the #1 crawler bottleneck and introduces local async DNS caching (C-ARES).
- **Spider Trap Defense**: Implements path depth limits, URL canonicalization, and per-domain crawl quotas.
- **SimHash Near-Duplicate Detection**: Explains how 64-bit Hamming distance detects mirrored websites with minor text modifications.
