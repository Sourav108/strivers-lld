# High-Level Design: URL Shortener (TinyURL)

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Client["Client Browser / Mobile App"] --> DNS["Cloudflare Anycast DNS"]
    DNS --> CDN["Global CDN Edge (Cache Hot Short Links)"]
    CDN --> LB["L7 Load Balancer (NGINX / ALB)"]
    LB --> Gateway["API Gateway (Rate Limiting & Auth)"]
    
    subgraph Services["Microservices Tier"]
        WriteSvc["URL Shortener Service (Writes)"]
        ReadSvc["Redirection Service (Reads)"]
        KGS["Key Generation Service (KGS)"]
    end

    Gateway -->|POST /api/v1/shorten| WriteSvc
    Gateway -->|GET /{short_key}| ReadSvc
    WriteSvc <--> KGS

    subgraph StorageTier["Data & Cache Tier"]
        KGS_DB["KGS Key Token DB (Pre-generated tokens)"]
        RedisCluster["Redis Cache Cluster (LRU Eviction)"]
        SQLCluster["Sharded PostgreSQL / DynamoDB"]
        Kafka["Kafka Event Stream (Analytics)"]
        AnalyticsWorker["Click Analytics Worker"]
        AnalyticsDB["ClickHouse / Cassandra (Metrics DB)"]
    end

    KGS --> KGS_DB
    ReadSvc --> RedisCluster
    ReadSvc -->|Cache Miss| SQLCluster
    WriteSvc --> SQLCluster
    WriteSvc --> RedisCluster
    ReadSvc --> Kafka
    Kafka --> AnalyticsWorker --> AnalyticsDB
```

---

## 🔑 2. Key Generation Service (KGS) & Encoding

### Why Not Hash Function (MD5/SHA256)?
- `MD5(long_url)` produces a 128-bit hash (32 hex characters). Taking the first 7 characters causes hash collisions for different long URLs.
- Appending a sequential counter requires database locking and reveals URL creation rates to competitors.

### The KGS Pattern:
1. **Pre-generate 7-character Base62 Strings**:
   - Using Base62 characters (`[0-9, a-z, A-Z]`), a 7-character string gives:
   $$62^7 \approx \mathbf{3.5 \text{ Trillion unique URLs}}$$
2. **Key Generation Service (KGS)** runs in the background and populates two tables in MySQL/PostgreSQL:
   - `unused_keys`
   - `used_keys`
3. KGS loads a batch of **10,000 keys into memory** on startup.
4. When a write request arrives, `WriteSvc` retrieves an unused key in $O(1)$ without locking or hashing calculations.

---

## 🌐 3. API Endpoints

### Shorten URL
```http
POST /api/v1/urls
Content-Type: application/json

{
  "original_url": "https://en.wikipedia.org/wiki/Distributed_computing",
  "custom_alias": "dist-sys",  // Optional
  "expire_at": "2027-01-01T00:00:00Z" // Optional
}

Response: 201 Created
{
  "short_url": "https://tinyurl.com/a9Z1kx",
  "short_key": "a9Z1kx",
  "expires_at": "2027-01-01T00:00:00Z"
}
```

### Redirect URL
```http
GET /{short_key}

Response: 302 Found (or 301 Moved Permanently)
Location: https://en.wikipedia.org/wiki/Distributed_computing
```

---

## 🗄️ 4. Data Model

```sql
CREATE TABLE urls (
    short_key VARCHAR(7) PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
);
```
