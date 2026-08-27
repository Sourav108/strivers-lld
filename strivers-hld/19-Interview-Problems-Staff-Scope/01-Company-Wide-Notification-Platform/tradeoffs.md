# Trade-offs & Deep Dive: Company-Wide Notification Platform

## ⚖️ 1. Single Shared Queue vs Isolated Priority Topics

| Dimension | Single Shared Kafka Topic | Priority-Segregated Partitioned Topics |
|---|---|---|
| **Marketing Blast Impact** | 🔴 10M marketing blast blocks critical OTPs | 🟢 **Zero blast radius (P0 queue is empty & ready)** |
| **Worker Utilization** | Simple single worker fleet | Dedicated worker pools scaled by priority |
| **Decision** | ❌ Causes OTP delivery breaches | ✅ **Mandatory for Enterprise Multi-Tenancy** |
