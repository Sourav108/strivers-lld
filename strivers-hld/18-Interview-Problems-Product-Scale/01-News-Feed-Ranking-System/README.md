# High-Level Design: News Feed Ranking System

## 🏗️ 1. Multi-Stage Ranking Architecture

```mermaid
flowchart TD
    Client["User Opens Feed"] --> Gateway["API Gateway"]
    Gateway --> FeedAggregator["Feed Aggregator Service"]

    subgraph CandidateGeneration["Stage 1: Candidate Retrieval (10ms)"]
        FeedAggregator --> RedisTimeline["Redis Followee Timelines<br/>(500 candidates)"]
        FeedAggregator --> RecEngine["Vector Recommendations<br/>(500 candidates)"]
    end

    subgraph MLRankingPipeline["Stage 2: Scoring & Reranking (40ms)"]
        FeedAggregator --> FeatureStore["Feature Store (Redis)"]
        FeedAggregator --> MLScorer["ML Two-Tower Model"]
        MLScorer --> DiversityFilter["Diversity Filter"]
    end

    DiversityFilter --> Top50["Top 50 Ranked Posts"]
    Top50 --> Hydrator["Post Hydration (Media URLs)"]
    Hydrator --> Gateway
```

---

## ⚡ 2. The Hybrid Fan-out Pattern
- **Standard Users**: **Fan-out on Write** (injected directly into followers' Redis timeline cache).
- **Celebrities (> 50k Followers)**: **Fan-out on Read** (pulled dynamically during candidate retrieval and merged with cached timelines in memory).
