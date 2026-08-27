# High-Level Design: Distributed Web Crawler

## 🏗️ 1. High-Level Architecture

```mermaid
flowchart TD
    Frontier["URL Frontier<br/>(Priority & Politeness)"] --> FetcherFleet["Fetcher Fleet (Epoll / Go)"]
    
    subgraph FetcherInternal["Fetcher Subsystem"]
        DNS["Local Async DNS Cache (C-ARES)"]
        Robots["Robots.txt Cache (Redis)"]
        FetcherFleet <--> DNS
        FetcherFleet <--> Robots
    end

    FetcherFleet -->|HTTP GET| Internet["Target Web Servers"]
    FetcherFleet --> Parser["HTML Parser & Content Extractor"]
    
    Parser --> SimHashCheck{"Content Duplicate Check?<br/>(64-bit SimHash)"}
    SimHashCheck -->|New Content| S3Storage["Raw Document Store (S3)"]
    S3Storage --> Indexer["Search & Vector Indexing Pipeline"]

    Parser --> LinkExtractor["Outbound Link Extractor"]
    LinkExtractor --> BloomCheck{"URL Already Crawled?<br/>(Bloom Filter)"}
    BloomCheck -->|No| Frontier
    BloomCheck -->|Yes| Discard["Drop Discovered URL"]
```

---

## 🚦 2. Politeness & Priority Dual-Queue Hierarchy
- **Priority Queues (F1..Fn)**: Rank URLs by PageRank and domain authority.
- **Politeness Host Queues (B1..Bn)**: Ensure only 1 worker thread fetches from a specific domain at any given moment, strictly maintaining a 500ms delay between consecutive requests to the same server.
