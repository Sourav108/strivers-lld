# Organizational Constraints & Compliance: Global Payments

## 🏢 1. Geopolitical Banking Regulations & Data Sovereignty
- **European GDPR / BaFin & Indian RBI Mandates**: Regulators strictly mandate that financial transactions involving domestic bank accounts must have their primary records residing exclusively within physical domestic borders.
- **Compliance Architecture**: Ensure encryption keys (AWS KMS / Cloud HSM) and ledger partition nodes for German and Indian entities are geographically locked to Frankfurt and Mumbai data centers.
