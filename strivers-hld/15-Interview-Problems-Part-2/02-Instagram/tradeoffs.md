# Trade-offs & Deep Dive: Instagram

## ⚖️ 1. Direct App Server Uploads vs S3 Pre-signed URLs

| Dimension | Upload via App Server | Direct Upload via S3 Pre-signed URL |
|---|---|---|
| **App Server Memory & CPU** | 🔴 High (Holding 10k open 2MB file streams crashes servers) | 🟢 **Zero CPU overhead** (App server only issues 1KB JSON token) |
| **Network Bottleneck** | Double bandwidth (Client $\rightarrow$ App Svc $\rightarrow$ S3) | **Single direct transfer** (Client $\rightarrow$ S3) |
| **Security & Validation** | Validation in code | Strict pre-signed policy (size limit, mime-type lock) |
| **Recommendation** | ❌ Avoid for media | ✅ **Industry Standard** |

---

## 🚨 2. Sharding the Media Metadata Database

- **Partition Key**: Shard by `user_id` using **Consistent Hashing**.
- **Advantage**: All posts, profile metadata, and photos uploaded by a single user reside on the same database shard, making profile grid queries (`SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT 20`) ultra-fast single-shard lookups with zero scatter-gather overhead.
