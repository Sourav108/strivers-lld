# Scale Follow-ups: Observability & Reliability Engineering

## 🚀 1. What Changes at 10x Scale?
- **High Cardinality Metrics Explosion**: When developers add `user_id` or `order_id` as Prometheus metric labels, the number of distinct time-series explodes from thousands to millions, crashing Prometheus storage nodes.
- **Solution**: Enforce strict Prometheus label cardinality policies; push high-cardinality debugging attributes strictly into Distributed Traces (Jaeger / OpenTelemetry spans) and structured logs rather than metrics.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Telemetry Data Ingestion Costs**: Storing 100% of logs and traces at 500,000 QPS costs millions of dollars per month in Datadog/Elasticsearch bills.
- **Solution**: Implement **Tail-Based Dynamic Sampling**:
  - Buffer 100% of spans in worker memory for 30 seconds.
  - If the request ends with HTTP 5xx or latency $> p95$, retain and index 100% of the trace.
  - If the request is a healthy 200 OK, sample only 0.05% of traces.
