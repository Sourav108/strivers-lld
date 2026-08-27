# Trade-offs & Deep Dive: Monolith Migration

## ⚖️ 1. Application Dual-Writing vs CDC (Change Data Capture)

| Dimension | Application Dual-Writing (`db1.save() && db2.save()`) | Change Data Capture (CDC via Postgres WAL) |
|---|---|---|
| **Risk of Dual-Write Inconsistency**| 🔴 High (If app crashes between write 1 and 2, DBs diverge) | 🟢 **Zero dual-write inconsistency (Guaranteed by WAL commit)** |
| **Monolith Code Modification** | Requires touching complex legacy spaghetti code | **Zero changes to legacy monolith codebase** |
| **Decision** | ❌ Highly error-prone | ✅ **Industry Standard (Debezium + Kafka)** |
