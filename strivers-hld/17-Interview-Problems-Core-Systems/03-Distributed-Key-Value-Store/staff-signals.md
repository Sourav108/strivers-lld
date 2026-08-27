# Staff Signals: Distributed Key-Value Store

## 🎯 Staff-Level Grading Criteria:
- **LSM-Tree Internals**: Explains write amplification, SSTable compaction algorithms (Leveled vs Size-Tiered), and Bloom filters.
- **Anti-Entropy & Merkle Trees**: Articulates why Merkle trees prevent sending gigabytes of raw data over the network during background cluster repairs.
- **Split-Brain & Hinted Handoff**: Demonstrates how temporary write hints are buffered and replayed when disconnected nodes recover.
