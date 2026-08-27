# When NOT to Use: Synchronous Logging & Low-Threshold Alerting

## ❌ Top 2 Observability Disasters to Avoid:

1. **Synchronous File Logging in Critical Path**:
   - *Why*: Writing log lines synchronously to disk blocks application worker threads when the underlying EBS storage volume hits IOPS limits, causing immediate API timeouts across the entire cluster.
   - *Fix*: Push logs asynchronously to local in-memory ring buffers or stdout parsed by async agents (Vector / FluentBit).
2. **Alerting on Raw Symptoms instead of User-Impact SLOs (Alert Fatigue)**:
   - *Why*: Paging engineers at 3 AM for high CPU on 1 of 50 pods (when user p99 latency and error rates are completely normal) causes engineer burnout and leads teams to ignore real production outages.
   - *Staff Standard*: **Alert strictly on Multi-Window Multi-Burn-Rate SLO alerts** (e.g. "We are consuming 2% of our 30-day error budget in 1 hour").
