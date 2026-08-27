# Trade-offs & Deep Dive: Distributed Cache System

## ⚖️ 1. Client-Side Hashing vs Proxy-Based vs Cluster-Mode Hashing

| Architecture | Example | Latency | Routing Complexity | Node Failover |
|---|---|---|---|---|
| **Client-Side** | Memcached + libmemcached | 🚀 **Fastest (<0.5ms direct)** | Client calculates consistent hash | Requires client config update |
| **Proxy-Based** | Twemproxy / Envoy | 🟡 Moderate (Extra proxy hop) | Zero client logic needed | Proxy handles rerouting |
| **Server Cluster**| Redis Cluster (16k slots) | 🚀 **Fast (Direct + MOVED redirects)** | Client caches slot-to-node map | Automated master-replica failover |

---

## 🚨 2. Cache Invalidation & Stampede Mitigations

- **Probabilistic Early Expiration (XFetch Algorithm)**:
  - Rather than waiting for a hot key to strictly hit TTL 0 (causing 10,000 DB queries simultaneously), worker threads probabilistically recompute and refresh the cached value before it expires:
  $$\Delta - \beta \times \ln(\text{rand}()) > \text{TTL}$$
  - Guarantees exactly one thread refreshes the cache in the background while all other clients read the hot cache without delay!
