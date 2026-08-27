# When NOT to Use: Complex API Gateways & Service Meshes (Istio)

## ❌ When a Full Service Mesh / Complex Gateway is WRONG:

1. **Small Fleet (< 20 Microservices)**:
   - *Why*: Injecting an Envoy sidecar into every Kubernetes pod increases latency by $1\text{ms}–3\text{ms}$ per hop and consumes 25% extra cluster CPU and memory purely for sidecar telemetry.
   - *Better Choice*: Use **gRPC direct client-side load balancing** with standard Kubernetes DNS / headless services.
2. **Internal High-Throughput Batch Processing Pipelines**:
   - *Why*: Routing massive internal ETL data streams through an L7 API Gateway adds unnecessary serialization overhead and creates a single bottleneck.
