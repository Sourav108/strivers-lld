# Scale Follow-ups: Cloud Economics, FinOps & Repatriation

## 🚀 1. What Changes at 10x Scale?
- **Cloud Egress & Data Transfer Inflation**: At 100M users, AWS Data Transfer OUT costs become one of the highest line items on the company balance sheet.
- **Solution**: Aggressive CDN edge caching (Cloudflare / Fastly with Origin Shield), private AWS DirectConnect / GCP Cloud Interconnect links, and gzip/Brotli/Zstandard payload compression.

---

## 🌍 2. What Changes at 100x Scale? (Cloud Repatriation - 37signals / Dropbox)
- When cloud bills exceed \$50 Million/year, managing custom bare-metal data centers (Own your hardware / Colocation) becomes **$5\times$ cheaper** than AWS/GCP (as proven by Dropbox saving \$75M by migrating off AWS S3 onto their custom Diskotech storage architecture).
