# Staff Signals: Experimentation Platform

## 🎯 Staff-Level Grading Signals:
- **Zero-Network In-Memory Evaluation**: Rejects centralized RPC calls for feature flag evaluations and utilizes deterministic hashing (`Murmur3(user_id + salt)`).
- **Statistical Integrity (Sample Ratio Mismatch)**: Explains how Chi-Square tests run in real time to detect biased variant assignment before shipping flawed features.
- **Client Battery & Bandwidth Optimization**: Batches exposure telemetry events on mobile devices and flushes them periodically or upon app backgrounding.
