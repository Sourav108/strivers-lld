# Scale Follow-ups: Load Balancing & Proxies

## 🚀 1. What Changes at 10x Scale?
- **Load Balancer Memory Exhaustion**: L7 reverse proxies (NGINX/ALB) maintain connection pools to both clients and upstream backends. With 500k concurrent client streams, reverse proxies saturate RAM.
- **Solution**: Split load balancing into a two-tier hierarchy:
  1. **Tier 1 (L4 Load Balancer / Maglev / AWS NLB)**: Ultra-fast packet forwarding across IP/ports.
  2. **Tier 2 (L7 Envoy / NGINX)**: Content routing, SSL termination, and rate limiting.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Global Anycast BGP Routing**: Instead of relying on GeoDNS (which suffers from slow DNS TTL propagation and ISP caching delays), deploy **BGP Anycast routing** so network routers automatically route user packets to the closest healthy edge ingress node.
