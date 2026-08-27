# Organizational Constraints & Strategy: Monolith Migration

## 🏢 1. Managing Developer Velocity During Migration
- **The "Feature Freeze" Failure Mode**: Asking product managers to stop building new business features for 18 months while engineers rewrite the backend leads to executive revolt.
- **Staff Solution**: Carve out independent domain teams (e.g. 80% feature work, 20% migration allocation) and extract 1 domain at a time over quarterly milestones.

---

## 👥 2. Team Skill Gaps & Up-Skilling
- Legacy team consists of monolithic Ruby/PHP developers unfamiliar with distributed tracing, Kubernetes, and event streaming.
- Establish an **Embedded Enablement Guild** with senior platform engineers pairing directly with product engineers during initial microservice rollout.
