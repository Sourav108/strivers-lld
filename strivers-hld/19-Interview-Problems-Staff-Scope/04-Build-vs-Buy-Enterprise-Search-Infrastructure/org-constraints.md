# Organizational Constraints & Migration: Search Infrastructure

## 🏢 1. Phased 3-Month Migration Strategy
- **Month 1**: Deploy OpenSearch cluster; set up CDC pipeline from PostgreSQL product database via Kafka to index 50M products.
- **Month 2**: Dark Launch / Shadow Search. Replay 100% of live user queries against both Algolia and OpenSearch; compare NDCG@10 relevance scores and p99 latency in real time.
- **Month 3**: Gradual canary traffic shift: 5% $\rightarrow$ 25% $\rightarrow$ 100% live traffic to OpenSearch; terminate Algolia annual contract (\$4.2M annual savings).
