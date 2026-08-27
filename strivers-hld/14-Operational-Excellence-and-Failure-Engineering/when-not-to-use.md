# When NOT to Use: Aggressive Retries without Backpressure

## ❌ Why Naive Retries are a Leading Cause of Production Outages:

1. **The Retry Storm Disaster**:
   - *Scenario*: A database temporarily spikes to 95% CPU, causing 5% of queries to timeout after 2 seconds.
   - *Naive Code*: Each application server catches the timeout and immediately retries the query up to 3 times.
   - *Result*: The database now receives **$3\times$ more load** during an outage, turning a minor transient latency blip into a catastrophic, permanent total cluster collapse!
2. **Staff-Level Rule of Thumb**:
   - **Never retry without exponential backoff and randomized jitter**.
   - **Never retry non-idempotent operations (POST /charge)**.
   - **Enforce a cluster-wide Retry Budget** (e.g., maximum 10% of total requests can be retries; reject further retries if the budget is exhausted).
