# Staff-Level Requirements: Multi-Tenant Distributed Rate Limiter

## 📋 The Staff Prompt
*"Design a multi-tenant enterprise rate limiting infrastructure that protects internal and external APIs across 50+ services, supporting dynamic tier quotas, tenant isolation (noisy neighbor protection), and sub-1ms evaluation overhead."*

---

## 🎯 Functional Requirements (FR)
1. **Multi-Tenant Identification**: Throttle by API Key, Tenant ID, IP, and Endpoint Route (`/v1/payments` vs `/v1/search`).
2. **Tier-Based Dynamic Quotas**: Support dynamic rule updates without redeploying gateways (e.g. Free Tier: 100 req/min, Enterprise: 50,000 req/min).
3. **HTTP Standard Compliance**: Return 429 Too Many Requests with standard `X-RateLimit-*` headers.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency Overhead**: Rate limit evaluation must take **`< 1ms`** (p99).
2. **Noisy Neighbor Isolation**: A burst of traffic from Tenant A must never starve or degrade rate check latency for Tenant B.
3. **Fail-Open Policy**: If the rate limit cache cluster experiences partition/failure, default to **fail-open** with alerts.
