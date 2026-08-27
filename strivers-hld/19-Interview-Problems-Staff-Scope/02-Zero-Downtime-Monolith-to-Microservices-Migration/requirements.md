# Staff Prompt & Ambiguous Framing: Zero-Downtime Migration

## 📋 The Ambiguous Staff Prompt
*"We have a 12-year-old monolithic Ruby-on-Rails application and a 20TB primary PostgreSQL database powering our core e-commerce checkout. Deployments take 4 hours, schema changes require scheduled 2 AM weekend maintenance windows, and a deadlock in the inventory module periodically crashes our entire checkout site. Design a multi-year, zero-downtime architecture and organizational strategy to decompose this monolith into independent domain microservices without ever taking the site offline."*

---

## 🎯 How a Staff Engineer Clarifies & Frames Scope:
1. **Zero Customer Impact**: $100\%$ uptime during database cutovers; zero lost orders or double charges.
2. **Decomposition Boundary**: Extract the highest-risk, highest-value domain first (**Orders & Checkout**).
3. **Data Migration Pattern**: Implement **Strangler Fig Pattern + Change Data Capture (CDC via Debezium) + Dark Launch Shadow Traffic**.
