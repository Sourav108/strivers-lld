# Staff Prompt & Ambiguous Framing: Enterprise Search Infrastructure

## 📋 The Ambiguous Staff Prompt
*"Our e-commerce marketplace has grown to 50 Million product SKUs, 500M monthly searches, and \$4B in GMV. We are currently using Algolia (hosted SaaS), but our monthly SaaS bill just crossed \$350,000/month (\$4.2M/year), and Algolia's fixed indexing schemas prevent our Data Science team from injecting custom ML embedding re-rankers. The CTO asked you to lead a Build-vs-Buy evaluation and present a 3-year technical strategy: Do we stay on Algolia, migrate to managed Elastic Cloud, or build a custom OpenSearch/Lucene cluster in-house?"*

---

## 🎯 How a Staff Engineer Frames the Decision:
1. **Total Cost of Ownership (TCO) Analysis**: Compute 3-year SaaS costs vs Cloud infrastructure + SRE headcount costs.
2. **Business Differentiator Test**: Search relevance directly impacts conversion rate (a 1% conversion increase = \$40M incremental GMV). Search is a **core competitive differentiator**, justifying an in-house platform.
3. **Architecture Proposal**: Self-hosted **OpenSearch / Elasticsearch cluster on AWS EKS** with custom Vector Search (HNSW) and hybrid BM25 lexical ranking.
