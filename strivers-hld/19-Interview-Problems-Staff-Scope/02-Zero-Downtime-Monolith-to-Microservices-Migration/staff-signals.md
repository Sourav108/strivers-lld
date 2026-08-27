# Staff Signals: Zero-Downtime Monolith Migration

## 🎯 Staff-Level Grading Signals:
- **Strangler Fig Mastery**: Detailed understanding of routing proxies, dark launching, and data reconciliation without scheduled maintenance downtime.
- **CDC WAL Architecture**: Explains why database transaction log streaming is strictly superior to double application writes.
- **Rollback Readiness**: Proves how to maintain a reverse-sync pipeline (New DB $\rightarrow$ Old Monolith DB) during the cutover phase so traffic can be rolled back instantly if an unexpected bug appears.
