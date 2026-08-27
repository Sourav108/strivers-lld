# Scale Follow-ups: Security & Global Compliance

## 🚀 1. What Changes at 10x Scale?
- **JWT Revocation Bottleneck**: Validating JWT tokens statelessly is fast, but revoking a compromised user session requires checking a centralized blacklist.
- **Solution**: Use **Short-Lived Access Tokens (5–15 minutes)** + Refresh Tokens stored in Redis, coupled with localized Bloom Filters on API Gateways for instant session revocation checks.

---

## 🌍 2. What Changes at 100x Scale & Multi-Region Expansion?
- **Global Data Residency (GDPR / HIPAA / CCPA)**:
  - Encryption keys must be region-specific (AWS KMS in Frankfurt for EU, KMS in Virginia for US).
  - PII (Personally Identifiable Information) must be anonymized or tokenized before leaving regional borders for analytics.
