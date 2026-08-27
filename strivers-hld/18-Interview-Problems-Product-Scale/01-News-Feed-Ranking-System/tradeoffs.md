# Trade-offs & Deep Dive: News Feed Ranking System

## ⚖️ 1. Real-Time ML Inference vs Pre-computed Timeline Feeds

| Dimension | Pre-computed Timeline Cache | Real-Time ML Ranking Pipeline |
|---|---|---|
| **Feed Latency** | 🚀 **< 5ms** | $\sim 50\text{ms} - 80\text{ms}$ |
| **Personalization Quality** | 🔴 Low (Static reverse-chronological order) | 🟢 **State-of-the-art engagement & relevance** |
| **Compute Cost** | Low CPU, high RAM for Redis | High GPU/CPU inference cost |
| **Decision** | Use Pre-computed cache for candidate filtering; use **Real-Time ML for Top-50 re-ranking**. |
