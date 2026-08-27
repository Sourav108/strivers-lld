# Staff Signals & Evaluation Rubric: URL Shortener @ 1B Users

## 🎯 What Interviewers & Bar-Raisers Actually Score:

```
+------------------------------------+---------------------------------------------------------------+
| Candidate Level                   | Observable Behavioral & Architectural Signals                 |
+------------------------------------+---------------------------------------------------------------+
| L4 (Mid-Level)                     | Implements Base62 encoding and standard Redis caching.       |
|                                    | Struggles with multi-region replication and SPOF in KGS.      |
+------------------------------------+---------------------------------------------------------------+
| L5 (Senior)                        | Designs KGS token ranges with ZooKeeper. Calculates capacity  |
|                                    | accurately. Implements Kafka for asynchronous click metrics.  |
+------------------------------------+---------------------------------------------------------------+
| L6+ (Staff / Principal)            | • Addresses multi-region Anycast edge termination.            |
|                                    | • Explains TCO of Edge caching vs Centralized DB queries.      |
|                                    | • Handles custom vanity domain SSL certificate provisioning.  |
|                                    | • Defines strict SLOs & circuit breaker fallbacks.           |
+------------------------------------+---------------------------------------------------------------+
```
