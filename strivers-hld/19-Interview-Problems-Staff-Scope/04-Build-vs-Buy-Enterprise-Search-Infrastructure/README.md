# High-Level Design: Enterprise Search Infrastructure

## 🏗️ 1. Hybrid Lexical + Vector Search Architecture

```mermaid
flowchart TD
    UserQuery["Search Query"] --> Gateway["API Gateway"]
    Gateway --> SearchService["Search Orchestrator"]

    subgraph QueryProcessing["Query Pipeline"]
        SearchService --> NLPParser["NLP & Synonyms"]
        SearchService --> EmbeddingModel["Vector Embeddings (BERT)"]
    end

    subgraph SearchTier["OpenSearch Cluster"]
        SearchService -->|1. BM25 Query| LexicalSearch["BM25 Lexical Search"]
        SearchService -->|2. k-NN Query| VectorSearch["HNSW Vector Search"]
    end

    LexicalSearch & VectorSearch --> HybridRanker["RRF & ML Re-ranker"]
    HybridRanker --> TopResults["Top 50 Results"]
    TopResults --> Gateway
```
