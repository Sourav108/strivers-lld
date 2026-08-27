# High-Level Design: Real-Time Ride-Matching Engine

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Driver["Driver App (GPS every 4s)"] -->|gRPC Stream| LocationGW["Location Ingestion Gateway Fleet"]
    Rider["Rider App (Request Ride)"] -->|HTTPS| MatchGW["Matching API Gateway"]

    subgraph RealTimePipeline["Real-Time Streaming & Indexing"]
        LocationGW --> Kafka["Kafka Location Stream (Partitioned by City)"]
        Kafka --> Ingester["Location Ingester Workers"]
        Ingester --> H3Cache[("In-Memory Hexagonal Spatial Index (Uber H3 / Redis)")]
    end

    subgraph MatchmakingCore["Matchmaking & Dispatch Engine"]
        MatchGW --> MatchEngine["Matchmaking Engine"]
        MatchEngine <--> H3Cache
        MatchEngine --> Redlock["Distributed Lock Service (Redis Redlock)"]
        MatchEngine --> DispatchWorker["Dispatch Worker"]
    end

    DispatchWorker -->|Push Offer| LocationGW
    MatchEngine --> TripDB[("Trip Master Database (PostgreSQL / Docstore)")]
```

---

## 🗺️ 2. Spatial Indexing: Why Uber Uses Hexagons (H3)
- In a square grid, diagonal cells are $\sqrt{2}\times$ further away than orthogonal neighbors.
- In **Uber H3 Hexagonal Grid**, all 6 neighboring cells are at the exact same geographic distance, eliminating direction bias in distance computations and k-ring radius expansions!
