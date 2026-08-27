# Staff Prompt & Ambiguous Framing: Company-Wide Notification Platform

## 📋 The Ambiguous Staff Prompt
*"We currently have 40 different engineering teams sending emails, push notifications, and SMS directly through their own ad-hoc Twilio and SendGrid accounts. It’s costing us \$12M/year, users are getting spammed with 15 duplicate marketing emails a day, and security flagged that we have no unified audit trail or GDPR opt-out enforcement. Design a unified, company-wide notification platform that every team must adopt."*

---

## 🎯 How a Staff Engineer Clarifies & Frames Ambiguity:

1. **Business & Financial Goals**: Consolidate enterprise vendor contracts to cut \$4M/year; guarantee GDPR opt-out compliance under penalty of law.
2. **Platform Multi-Tenancy**: 40 internal product teams as tenants with independent quota allocations, priority queues, and template registries.
3. **Core SLAs**:
   - `CRITICAL` (OTP/2FA SMS): Delivered in $< 3\text{ seconds}$ with automated multi-vendor failover (Twilio $\rightarrow$ MessageBird).
   - `STANDARD` (Order Updates): Delivered in $< 30\text{ seconds}$.
   - `BULK` (Marketing Blasts): Throttled to prevent downstream vendor rate-limit exhaustion.
