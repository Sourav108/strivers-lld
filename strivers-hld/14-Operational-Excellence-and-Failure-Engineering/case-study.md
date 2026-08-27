# Case Study: Amazon Prime Day Load Shedding & Netflix Chaos Kong

## 🏢 Context: Surviving 100x Traffic Spikes and Total Cloud Zone Failures

During Amazon Prime Day, traffic surges by orders of magnitude in seconds. Simultaneously, public cloud providers experience unexpected hardware outages.

```mermaid
flowchart TD
    Traffic["100x Traffic Surge"] --> Edge["Amazon Edge Envoy / CloudFront"]
    Edge --> LoadShedder["Token-Bucket Load Shedder"]
    LoadShedder -->|Tier 1 (Checkout)| CheckoutSvc["Checkout Service (100% Guaranteed Capacity)"]
    LoadShedder -->|Tier 3 (Reviews/Recommendations)| DropWorker["Degraded / Static Recommendations"]
```

---

## 🛠 Engineering Innovations

### 1. Amazon's Tiered Graceful Degradation
- If Amazon's recommendation engine is overwhelmed, the product page does not crash.
- The page simply omits "Customers who bought this also bought..." or renders a static pre-computed cache, allowing customers to complete their purchases without interruption.

### 2. Netflix Chaos Kong: Killing Entire AWS Regions
- To ensure Netflix can survive the loss of an entire AWS Region (e.g., `us-east-1` going completely dark), Netflix engineers execute **Chaos Kong** tests in production.
- Chaos Kong forcibly reroutes 100% of North American subscriber video streaming traffic to `us-west-2` and `eu-west-1` in under 7 minutes, verifying that multi-region active-active capacity planning actually works.
