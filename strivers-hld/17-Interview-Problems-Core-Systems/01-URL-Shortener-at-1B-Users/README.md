# High-Level Design: URL Shortener @ 1B Users

## 🏗️ 1. Global Multi-Region Architecture

```mermaid
flowchart TD
    Client["Global Client Browser / App"] --> Anycast["BGP Anycast Ingress (Cloudflare Edge)"]
    Anycast --> EdgeCache["Edge PoP In-Memory Cache Tier"]
    
    EdgeCache -->|Edge Cache Hit (Sub-5ms)| ReturnClient["302 Redirect to Long URL"]
    EdgeCache -->|Edge Cache Miss| RegionalGateway["Regional API Gateway (Envoy)"]

    subgraph RegionalCluster["Regional Application Cluster (US / EU / APAC)"]
        WriteSvc["URL Write Service"]
        ReadSvc["Redirection Service"]
        LocalRedis["Regional Redis Read Cluster"]
    end

    RegionalGateway -->|POST /api/v1/shorten| WriteSvc
    RegionalGateway -->|GET /:short_key| ReadSvc
    ReadSvc --> LocalRedis

    subgraph GlobalDataTier["Global Storage & Token Pipeline"]
        KGSCluster["Distributed Key Generation Service (Zookeeper / etcd coordinated)"]
        GlobalDB[("CockroachDB / DynamoDB Multi-Region Tables")]
        KafkaAnalytics["Kafka Clickhouse Telemetry Pipeline"]
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
