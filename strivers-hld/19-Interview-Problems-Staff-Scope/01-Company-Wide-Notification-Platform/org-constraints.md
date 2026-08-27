# Organizational Constraints & Migration: Notification Platform

## 🏢 1. Phased 4-Quarter Adoption Roadmap
- **Q1 (Core Infra & Top 3 Teams)**: Build multi-tenant gateway, Kafka priority topics, and migrate the high-volume Auth (OTP) and Billing teams.
- **Q2 (Self-Service Dashboard & Templates)**: Provide marketing and growth teams with visual template editors, localization tools, and GDPR opt-out controls.
- **Q3 (Deprecation & Gateway Sunset)**: Disallow creation of direct third-party Twilio/SendGrid sub-accounts; enforce all traffic through platform SDKs.
- **Q4 (Automated Multi-Vendor Cost Optimization)**: Deploy smart vendor routing to dynamically route SMS via the cheapest compliant provider per country.

---

## 💰 2. Chargeback Model (Internal Cloud Economics)
- The platform tracks exact per-tenant message volumes in ClickHouse and provides automated internal chargeback invoices to each product team's departmental budget, incentivizing teams to eliminate spammy duplicate blasts.
