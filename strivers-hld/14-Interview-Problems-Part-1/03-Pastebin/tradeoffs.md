# Trade-offs & Deep Dive: Pastebin

## ⚖️ 1. Storing Blobs in S3 vs In-Database (PostgreSQL `TEXT`/`BYTEA`)

| Dimension | Amazon S3 / Object Store | Relational DB (PostgreSQL `TEXT`) |
|---|---|---|
| **Cost per GB** | 🟢 **~$0.023 / GB / month** | 🔴 ~$0.11 / GB / month (EBS SSD) |
| **Max Payload Size** | Up to 5 TB per object | Large rows cause table bloat and slow vacuuming |
| **Throughput Scaling**| Scales to 5,500 GET req/sec per prefix | Disk I/O bottlenecks on large queries |
| **Decision** | **Amazon S3 for Raw Content** | **Relational DB for Metadata only** |

---

## 🚨 2. Bottlenecks & Expiration Lifecycle Management

### 1. S3 Lifecycle Policies for Automatic Deletion
- Instead of executing millions of programmatic `DELETE /pastes/{id}` API requests from backend workers (which costs money and CPU), configure **S3 Lifecycle Expiration Rules**:
  - Tags like `ttl: 30d` trigger Amazon S3 to asynchronously purge expired objects from disk at zero compute cost.

### 2. Hot Paste Caching Tier
- Viral pastes (e.g., leaked announcements or popular source snippets) cause read spikes.
- **Solution**: Cache the full text payload directly in Redis / Cloudflare CDN edge with a 1-hour TTL. 99.9% of requests are served from edge memory without touching S3 or the database.
