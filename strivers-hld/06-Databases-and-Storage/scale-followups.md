# Scale Follow-ups: Databases, Storage & Data Sovereignty

## 🚀 1. What Changes at 10x Scale?
- **Connection Pool Starvation**: When 1,000 backend microservice instances open 20 connections each, the database receives 20,000 connections, crashing PostgreSQL CPU with process-switching overhead.
- **Solution**: Introduce **PgBouncer / AWS RDS Proxy** to multiplex 20,000 client connections onto 200 persistent backend database connections.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Data Sovereignty & GDPR Compliance**: User data belonging to EU citizens must physically reside within EU data centers and cannot be replicated to US servers.
- **Solution**: Implement **Geographic Data Sharding (Locality-Aware Partitioning)** in CockroachDB / Spanner, where partition keys include `country_code` to bind data storage to specific regional data centers.
