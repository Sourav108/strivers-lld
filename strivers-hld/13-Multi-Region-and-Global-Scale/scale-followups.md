# Scale Follow-ups: Multi-Region Global Topologies

## 🚀 1. What Changes at 10x Scale?
- **Inter-Region Data Egress Cloud Costs**: Cross-region AWS/GCP network egress costs ($0.02/GB) skyrocket when microservices in EU synchronously call databases in US.
- **Solution**: Enforce **Local In-Region Read Caches** and aggregate cross-region updates into compressed batch replication pipelines.

---

## 🌍 2. What Changes at 100x Scale?
- **Split-Brain Disaster Scenarios**: An undersea fiber optic cable cut severs network connectivity between North America and Europe. Both regions believe the other has died and try to accept conflicting financial writes.
- **Solution**: Deploy a **Third Tie-Breaker Region** (Witness Region containing only Raft/Paxos quorum consensus nodes, zero application data) to ensure only the partition with the strict majority can commit writes.
