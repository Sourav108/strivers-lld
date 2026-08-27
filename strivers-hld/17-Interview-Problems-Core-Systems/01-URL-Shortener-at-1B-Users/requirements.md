# Staff-Level Requirements: URL Shortener @ 1B Users

## 📋 The Staff Prompt
*"Design a globally distributed URL shortening and redirection platform supporting 1 Billion active users, enterprise custom domains, and real-time click telemetry with sub-10ms redirection latency."*

---

## 🎯 Functional Requirements (FR)
1. **URL Shortening**: Convert long URLs to short 7-character Base62 aliases (`tiny.one/a9Z1kx`).
2. **Global Redirection**: Redirect users with HTTP 302 in $< 10\text{ms}$ globally.
3. **Custom Domains & Vanity Aliases**: Enterprise customers can use their own custom domains (`go.uber.com/driver-signup`).
4. **Real-Time Analytics Pipeline**: Aggregated metrics on clicks, geolocations, browsers, and referrers.

---

## 🛡️ Non-Functional Requirements (NFR)
1. **Global Scale**: 1 Billion Monthly Active Users; 100 Billion monthly redirections ($\approx 40,000 \text{ QPS}$ avg, $100,000 \text{ QPS}$ peak).
2. **High Availability**: 99.999% uptime on redirection path.
3. **Zero Collision Guarantee**: Pre-generated tokens with zero single point of failure.
4. **Multi-Region Disaster Recovery**: Zero downtime even if an entire cloud region fails.
