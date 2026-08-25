# 01 - API Design, Versioning, and Security

## Core Idea

**API Design, Versioning, and Security** establishes robust, scalable, and maintainable communication contracts between software clients and backend services. Enterprise API engineering requires strict adherence to RESTful conventions, decoupling internal database entities using **Data Transfer Objects (DTOs)**, managing breaking changes via **API Versioning**, controlling payload sizes through **Pagination and Filtering**, and securing endpoints with **Rate Limiting** and **Role-Based Access Control (RBAC)**.

---

## 💡 Real-Life Analogy

### 🏢 The High-End Restaurant Menu & Concierge
- **API Contract (The Menu):** Dishes are clearly listed with prices and dietary tags (DTOs). Customers don't need to know the chef's secret prep recipes or database schema.
- **API Versioning (Seasonal Menu):** When introducing a new summer menu (V2), the restaurant continues honoring existing private reservations on the spring menu (V1) until the announced deprecation date.
- **Security & Rate Limiting (The Bouncer):** The door concierge verifies VIP passes (JWT Authentication) and limits entry to 5 guests per minute (Token Bucket Rate Limiting / HTTP 429) to prevent overcrowding the kitchen.

---

## 📐 Enterprise RESTful Architecture

```
                             +----------------------------------------+
                             |           CLIENT APPLICATION           |
                             +----------------------------------------+
                                                 |
                                  GET /api/v1/courses?page=1&limit=10
                                  Header: Authorization: Bearer <JWT>
                                                 v
                             +----------------------------------------+
                             |    API GATEWAY / SECURITY FILTERS      |
                             |    1. Authentication & RBAC            |
                             |    2. Token-Bucket Rate Limiter (429)  |
                             +----------------------------------------+
                                                 |
                                                 v
                             +----------------------------------------+
                             |             API CONTROLLER             |
                             |    - Validates Request DTO             |
                             |    - Routes to Versioned Service (V1)  |
                             +----------------------------------------+
                                                 |
                                                 v
                             +----------------------------------------+
                             |       DOMAIN ENTITY & REPOSITORY       |
                             |    - Executes Business Logic           |
                             |    - Converts Entity -> Response DTO   |
                             +----------------------------------------+
```

---

## ⚖️ API Versioning Strategies

| Strategy | Example | Pros | Cons |
|---|---|---|---|
| **URI Path Versioning** *(Recommended)* | `GET /api/v1/orders` | 🟢 Most explicit; easy to test in browser and cache. | Slightly modifies URL paths. |
| **Header Versioning** | `X-API-Version: 2` | 🟢 Keeps clean REST URLs. | Harder to explore in standard browser URL bars. |
| **Content Negotiation** | `Accept: application/vnd.company.v2+json` | 🟢 Strictly follows REST hypermedia standards. | Verbose; requires complex client headers. |

---

## ❌ Bad Design (Entity Leakage & Unbounded Queries)

```java
// ❌ Exposing raw database entity directly over HTTP
@RestController
public class BadUserController {
    @GetMapping("/getUsers") // ❌ Non-RESTful verb in URL, unversioned
    public List<UserEntity> getAllUsers() {
        // ❌ Returns entire 5,000,000 user database table in a single unpaginated request!
        // ❌ LEAKS sensitive internal fields: password_hash, ssn, stripe_secret_key!
        return userRepository.findAll();
    }
}
```

### What is wrong?
- ⚠️ **Critical Security Leak:** Internal password hashes, salt strings, and private keys leak directly to public clients.
- ⚠️ **Memory Exhaustion (OOM):** Fetching millions of unpaginated records crashes JVM heaps.
- ⚠️ **Brittle Tight Coupling:** Changing an internal database column immediately breaks all mobile and web clients.

---

## ✅ Good Design (DTO Contracts, Pagination, Versioning, & Rate Limiting)

```java
// 1. Immutable Contract Response DTO
public record UserResponseDTO(String id, String username, String email, String role) {}

// 2. Paginated Envelope Response
public record PagedResponse<T>(List<T> data, int page, int limit, long totalItems, int totalPages) {}

// 3. Structured Standard Error Schema
public record ApiErrorResponse(String timestamp, int status, String error, String message, String path) {}

// 4. Clean RESTful Versioned Controller with Rate Limiting
public class CourseApiControllerV1 {
    private final CourseService courseService;
    private final RateLimiter rateLimiter = new RateLimiter(5, 60_000); // 5 reqs per minute

    public ApiResponse<PagedResponse<CourseResponseDTO>> getCourses(String clientIp, int page, int limit) {
        // 1. Enforce Rate Limiting
        if (!rateLimiter.allowRequest(clientIp)) {
            return ApiResponse.error(429, "Too Many Requests. Retry after 60s.");
        }

        // 2. Validate Pagination
        int safeLimit = Math.min(limit, 50); // Bounded query protection

        // 3. Return Filtered DTOs
        PagedResponse<CourseResponseDTO> response = courseService.getCourses(page, safeLimit);
        return ApiResponse.success(200, response);
    }
}
```

### Why it better demonstrates the concept:
- ✅ **DTO Encapsulation:** Only public, sanitized fields are transmitted.
- ✅ **Bounded Pagination:** Hard limit of 50 items prevents database and network flooding.
- ✅ **Defensive Rate Limiting:** Returns standard HTTP 429 to mitigate brute-force and DDoS attacks.

---

## Java Classes

- **`UserResponseDTO` & `CourseResponseDTO`:** Immutable Record DTOs defining strict client response contracts.
- **`PagedResponse<T>`:** Generic pagination envelope capturing metadata (`page`, `limit`, `totalItems`, `totalPages`).
- **`ApiErrorResponse`:** RFC 7807 compliant standardized JSON error schema.
- **`TokenBucketRateLimiter`:** Sliding window rate limiter enforcing IP quotas with HTTP 429 backoff.
- **`CourseApiControllerV1` & `CourseApiControllerV2`:** Versioned controllers demonstrating backward compatibility.
- **`APIDesignAndSecurityExample` (Main Driver):** Tests and validates DTO conversion, paginated filtering, version evolution, and rate limit throttling.

---

## How It Works

1. Client makes an HTTP request to `/api/v1/courses?page=1&limit=2`.
2. `TokenBucketRateLimiter` validates the client's IP quota. If quota is exceeded, it immediately responds with `HTTP 429 Too Many Requests`.
3. The controller delegates to `CourseService`, querying the database and transforming internal `CourseEntity` models into clean `CourseResponseDTO` contracts.
4. The payload is encapsulated in a generic `PagedResponse` envelope and returned with `HTTP 200 OK`.

---

## When to Use

- **Public & Partner API Platforms:** Stripe, Twilio, GitHub API architectures.
- **Mobile & Web Microservices:** Decoupling frontend view requirements from backend persistence engines.
- **Multi-Tenant SaaS Systems:** Rate limiting per tenant tier (Free vs Pro quotas).

---

## When NOT to Use

- **High-Performance Internal IPC:** For internal low-latency microservice communication within the same Kubernetes cluster, prefer binary protocols like **gRPC (Protocol Buffers)** over verbose JSON REST APIs.

---

## LLD Takeaway

API Design, Versioning, and Rate Limiting are foundational for designing **API Gateways**, **E-Commerce Marketplaces**, and **Scalable SaaS Platforms** in Low-Level Design interviews. Always demonstrate DTO contracts, explicit HTTP status codes, and rate limiting protections.

---

## 🎯 Quick Summary

- **Core Idea:** Enterprise APIs require RESTful conventions, DTO separation, explicit versioning, bounded pagination, and rate limiting security.
- **Code Demonstrates:** URI versioned controllers, generic pagination envelopes, DTO transformations, and Token-Bucket rate limiting with HTTP 429.
- **LLD Takeaway:** Never expose database entities directly; always encapsulate data in DTOs, version APIs up-front, and throttle traffic with rate limiters.
- **Memorable Rule:** *"Version early, never leak database entities, paginate all collections, and rate-limit every endpoint."*
