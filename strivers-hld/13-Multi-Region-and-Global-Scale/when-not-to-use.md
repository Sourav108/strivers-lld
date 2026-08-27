# When NOT to Use: Global Multi-Region Active-Active Deployments

## ❌ When Multi-Region Active-Active is the WRONG Choice:

1. **Startups & Single-Geography Businesses (e.g. US-Only Fintech)**:
   - *Why*: If 98% of your customers live in the continental US, deploying an active-active region in Europe triples your AWS bill and operational complexity for no business benefit.
   - *Better Choice*: **Multi-Availability Zone (Multi-AZ)** within a single region (e.g. `us-east-1a`, `us-east-1b`, `us-east-1c`) with automated backup snapshots shipped to a secondary region.
2. **Applications Bound by Strict National Financial Regulations**:
   - *Why*: In many jurisdictions, banking laws strictly prohibit financial transactions from crossing geopolitical borders.
