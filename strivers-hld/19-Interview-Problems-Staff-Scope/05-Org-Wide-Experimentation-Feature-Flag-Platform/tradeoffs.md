# Trade-offs & Deep Dive: Experimentation Platform

## ⚖️ 1. Centralized Evaluation Service vs Embedded Local SDK Evaluation

| Dimension | Centralized Remote Evaluation Service | Embedded In-Process SDK Evaluation |
|---|---|---|
| **Evaluation Latency** | 🔴 15ms–40ms network RTT per feature flag | 🚀 **< 0.01ms (Pure RAM hash in CPU register)** |
| **Availability & Failure**| If service dies, all feature flags break | **100% Availability (Evaluates locally offline)** |
| **Bandwidth Cost** | Billions of RPCs hitting evaluation fleet | Lightweight 200KB manifest download every 60s |
| **Decision** | ❌ Severe latency overhead on user experience | ✅ **Universal Industry Standard (LaunchDarkly/Meta)** |
