# Requirements: Design a URL Shortener (TinyURL / Bitly)

## 📋 Functional Requirements (FR)
1. **URL Shortening**: Given a long URL (e.g. `https://en.wikipedia.org/wiki/Distributed_computing`), the system generates a unique, shorter alias (e.g. `https://tinyurl.com/a9Z1kx`).
2. **Redirection**: When a user navigates to the short link, the service redirects them to the original long URL with HTTP 301/302 status.
3. **Custom Aliases & Expiration**: Users can optionally specify a custom alias (e.g. `tinyurl.com/my-link`) and an optional expiration date.
4. **Analytics**: System tracks click count, referrer source, and geographic location.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Ultra-Low Latency**: Redirection must execute in `< 15ms` (p99).
2. **High Availability**: The redirection service must maintain **99.99% uptime** (Read operations must never fail).
3. **Read-Heavy Workload**: Read requests heavily outweigh write requests (typically $100:1$ read-to-write ratio).
4. **Non-Guessable & Collision-Free**: Short URLs should not be easily sequential or guessable to prevent enumeration scraping.

---

## 🚫 Out of Scope
- User authentication and paid billing tiers.
- Real-time malicious phishing link malware scanning.
