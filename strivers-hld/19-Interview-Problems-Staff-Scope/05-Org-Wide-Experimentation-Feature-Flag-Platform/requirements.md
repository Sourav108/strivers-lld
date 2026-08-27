# Staff Prompt & Ambiguous Framing: Org-Wide Experimentation Platform

## 📋 The Ambiguous Staff Prompt
*"Our company wants to run 1,000 concurrent A/B experiments across web, iOS, Android, and backend microservices simultaneously. Currently, teams hardcode feature flags or use ad-hoc database queries, causing 50ms latency spikes on app startup and statistical sample ratio mismatches (SRM). Design a high-performance, org-wide experimentation and feature flagging platform that evaluates user variants in sub-1ms local SDK memory without blocking network calls, while guaranteeing statistical confidence and metric ingestion."*

---

## 🎯 How a Staff Engineer Frames the Problem:
1. **Zero Network Call Evaluation**: Feature flags and variant bucket assignment must evaluate in **`< 0.05ms` locally in client RAM** via consistent hashing (`Murmur3(user_id + experiment_salt) % 100`).
2. **Dynamic Rule Distribution**: Experiment configurations (rules, targeting filters, traffic percentages) are broadcast globally to all clients via CDN-cached rule manifests or SSE streams.
3. **Statistical Ingestion Pipeline**: Asynchronously capture exposure events to compute p-values, sample ratio mismatch (SRM), and metrics lift in ClickHouse/Snowflake.
