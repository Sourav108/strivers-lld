# High-Level Design: Real-Time Ride-Matching Engine

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Driver["Driver App (GPS)"] -->|gRPC| LocationGW["Location Gateway"]
    Rider["Rider App"] -->|HTTPS| MatchGW["Match API Gateway"]

    subgraph StreamingTier["Location Pipeline"]
        LocationGW --> Kafka["Kafka Stream (City Topic)"]
        Kafka --> Ingester["Location Ingesters"]
        Ingester --> H3Cache[("Uber H3 Spatial Index (Redis)")]
    end

    subgraph MatchmakingCore["Match Engine"]
        MatchGW --> MatchEngine["Matching Engine"]
        MatchEngine <--> H3Cache
        MatchEngine --> Redlock["Redlock (15s Lock)"]
        MatchEngine --> DispatchWorker["Dispatch Worker"]
    end

    DispatchWorker -->|Push Offer| LocationGW
    MatchEngine --> TripDB[("Trip Master DB (Postgres)")]
```

---

## 🗺️ 2. Spatial Indexing: Why Uber Uses Hexagons (H3)
- In a square grid, diagonal cells are $\sqrt{2}\times$ further away than orthogonal neighbors.
- In **Uber H3 Hexagonal Grid**, all 6 neighboring cells are at the exact same geographic distance, eliminating direction bias in distance computations and k-ring radius expansions!
