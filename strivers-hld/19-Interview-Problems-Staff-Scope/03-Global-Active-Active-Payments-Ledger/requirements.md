# Staff Prompt & Ambiguous Framing: Global Active-Active Payments Ledger

## 📋 The Ambiguous Staff Prompt
*"We are a global fintech operating in 40 countries across North America, Europe, and Asia. When an entire AWS region went down last Black Friday, our single-primary Postgres architecture stalled, costing us \$35M in lost gross merchandise volume. The VP of Infrastructure mandated that our payments ledger must operate in a Global Active-Active topology where any region can accept writes and authorize payments with sub-200ms latency, zero double-spending, and 99.999% availability. Design the architecture."*

---

## 🎯 How a Staff Engineer Clarifies & Frames Ambiguity:
1. **The CAP / PACELC Challenge**: In a global network, speed of light between US and Singapore is $\sim 180\text{ms}$. Synchronous 2PC cross-region locking would destroy transaction latency.
2. **Staff Architectural Strategy**:
   - **Account Geographic Partitioning (Home Region Affinity)**: 98% of users initiate payments in their local region. Route writes to the user's home region with local multi-AZ Raft/Spanner quorums ($< 10\text{ms}$).
   - **Global Two-Phase Commit with TrueTime for Cross-Border Transfers**: Reserve distributed Spanner transactions strictly for rare inter-region user-to-user transfers.
