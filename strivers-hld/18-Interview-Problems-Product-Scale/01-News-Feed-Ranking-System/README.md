# High-Level Design: News Feed Ranking System

## 🏗️ 1. Multi-Stage Ranking Architecture

```mermaid
flowchart TD
    Client["User Opens Feed"] --> Gateway["API Gateway"]
    Gateway --> FeedAggregator["Feed Aggregator Service"]

    subgraph CandidateGeneration["Stage 1: Fast Candidate Retrieval (10ms)"]
        FeedAggregator --> RedisTimeline["Redis Followee Timelines (500 items)"]
        FeedAggregator --> RecEngine["Vector Search / Recommendations (500 items)"]
    end

    subgraph MLRankingPipeline["Stage 2 & 3: Heavy Scoring & Reranking (40ms)"]
        FeedAggregator --> FeatureStore["Real-Time Feature Store (Redis / Feast)"]
        FeedAggregator --> MLScorer["ML Ranking Model (Two-Tower / GBDT)"]
        MLScorer --> DiversityFilter["Business Rules & Diversity Filter (Deduplicate creators)"]
    end

    DiversityFilter --> Top50["Top 50 Ranked Posts"]
    Top50 --> Hydrator["Post Hydration Service (Text + Media URLs)"]
    Hydrator --> Gateway
```

---

## ⚡ 2. The Hybrid Fan-out Pattern
- **Standard Users**: **Fan-out on Write** (injected directly into followers' Redis timeline cache).
- **Celebrities (> 50k Followers)**: **Fan-out on Read** (pulled dynamically during candidate retrieval and merged with cached timelines in memory).
