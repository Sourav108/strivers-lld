# When NOT to Use: Microservices Decomposition

## ❌ Scenarios Where Microservices Break Organizations:

1. **Shared Database Microservices (The Anti-Pattern)**:
   - *Why*: Creating 15 separate microservices that all query the same monolithic database creates the worst architectural nightmare: distributed failure domains with tightly coupled database schemas.
   - *Rule*: If services cannot own their independent datastores, **keep them as modules within the same codebase**.
2. **High Inter-Service Data Dependencies**:
   - *Why*: If Service A must make 10 synchronous REST calls to Services B, C, D, and E to fulfill a single user request, network latency balloons and the cumulative availability drops exponentially ($A_{total} = A_1 \times A_2 \times \dots \times A_N$).
