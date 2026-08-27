# High-Level Design: URL Shortener @ 1B Users

## 🏗️ 1. Global Multi-Region Architecture

```mermaid
flowchart TD
    Client["Client Browser"] --> Anycast["Anycast Ingress (Edge PoP)"]
    Anycast --> EdgeCache["Edge In-Memory Cache"]
    
    EdgeCache -->|Hit: < 5ms| ReturnClient["302 Redirect"]
    EdgeCache -->|Miss| RegionalGateway["Regional Gateway (Envoy)"]

    subgraph RegionalCluster["Regional Application Cluster"]
        WriteSvc["Write Service"]
        ReadSvc["Redirect Service"]
        LocalRedis["Redis Read Cache"]
    end

    RegionalGateway -->|POST /shorten| WriteSvc
    RegionalGateway -->|GET /:short_key| ReadSvc
    ReadSvc --> LocalRedis

    subgraph GlobalDataTier["Global Storage Tier"]
        KGSCluster["KGS Token Cluster<br/>(etcd range leases)"]
        GlobalDB[("Multi-Region DB<br/>(DynamoDB / Spanner)")]
        KafkaAnalytics["Kafka + ClickHouse Analytics"]
    end

    WriteSvc <--> KGSCluster
    WriteSvc --> GlobalDB
    ReadSvc -->|On Miss: Query DB| GlobalDB
    ReadSvc --> KafkaAnalytics
```

---

## 🔑 2. Multi-Region Key Generation Service (KGS)

- To prevent multi-region token conflicts, KGS nodes are assigned **non-overlapping token ranges** via ZooKeeper/etcd (e.g. Node 1 gets tokens $1\dots 10^7$, Node 2 gets $10^7+1\dots 2\times 10^7$).
- Nodes dispense Base62 tokens in $O(1)$ RAM without distributed network locking!
