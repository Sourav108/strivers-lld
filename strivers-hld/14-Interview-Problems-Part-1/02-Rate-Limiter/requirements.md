# Requirements: Design a Distributed Rate Limiter

## 📋 Functional Requirements (FR)
1. **Limit Requests by Identifier**: Throttle incoming client requests based on IP address, User ID, or API Key (e.g., max 100 requests per minute).
2. **Standard HTTP Response**: Return **HTTP 429 Too Many Requests** when rate limit is exceeded, along with headers:
   - `X-RateLimit-Limit`: Maximum requests allowed in window.
   - `X-RateLimit-Remaining`: Remaining request quota.
   - `X-RateLimit-Reset`: Unix timestamp until quota reset.
3. **Configurable Rules**: Support dynamic tier-based rate rules (e.g. Free Tier: 50 req/min, Premium Tier: 1,000 req/min).

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency Overhead**: The rate limiting check must add **`< 2ms`** to overall API response time.
2. **High Accuracy & Concurrency**: Prevent race conditions when concurrent requests arrive simultaneously from distributed load balancers.
3. **Fault Tolerance & Fail-Open**: If the rate limiter cache cluster goes down, traffic should **fail-open** (allow requests through) rather than blocking legitimate users.
4. **Distributed Scalability**: Support millions of concurrent clients across multi-region server clusters.
