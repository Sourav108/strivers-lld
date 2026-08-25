# 01 - Exception Handling in Low-Level Design

## Core Idea

**Exception Handling** in Low-Level Design is the architectural practice of designing systems that gracefully anticipate, detect, isolate, and recover from runtime failures without crashing or leaving data in inconsistent states. Effective error handling separates the "happy path" from error recovery pipelines, utilizing **Fail-Fast** validation (protecting data integrity), **Fail-Safe** degradation (maintaining system availability), and expressive **Custom Domain Exceptions**.

---

## 💡 Real-Life Analogy

### 💳 Amazon Payment Gateway & Airplane Redundancy
- **Fail-Fast (Payment Gateway Validation):** If a user enters an expired credit card or a negative checkout amount, the payment engine stops immediately (`throw new InvalidCardException()`) to prevent fraudulent or corrupted database writes.
- **Fail-Safe (Engine Backup / Product Catalog):** If the primary recommendation algorithm service crashes during checkout, the website does not display a blank page; instead, it falls back to displaying a pre-cached list of top-selling products.

---

## ⚖️ Fail-Fast vs. Fail-Safe Architectures

| Dimension | Fail-Fast Strategy | Fail-Safe Strategy |
|---|---|---|
| **Core Goal** | Immediate error detection to preserve **data integrity**. | Graceful degradation to preserve **system availability**. |
| **Action on Error** | Halts execution immediately; throws an exception. | Catches error and returns a safe fallback/default response. |
| **System State** | Never allows corrupt or invalid states into memory/DB. | May operate on slightly stale or degraded data. |
| **Best Used For** | Financial transactions, inventory deductions, authentication. | Search recommendations, social media feeds, live telemetry. |

---

## 🏛️ Checked vs. Unchecked vs. Custom Exceptions

```
                         +-----------------------+
                         |       Throwable       |
                         +-----------------------+
                                    ^
                   +----------------+----------------+
                   |                                 |
         +-------------------+             +-------------------+
         |     Exception     |             |       Error       |
         | (Checked / Recov) |             | (JVM Fatal State) |
         +-------------------+             +-------------------+
                   ^
                   |
         +-------------------+
         | RuntimeException  |
         | (Unchecked / Bug) |
         +-------------------+
```

| Exception Type | Hierarchy | Compiler Enforced? | Purpose in LLD |
|---|---|---|---|
| **Checked Exception** | `extends Exception` | ✅ **Yes** (`try-catch` / `throws`) | Recoverable external I/O failures (e.g. `PaymentTimeoutException`, `NetworkIOException`). |
| **Unchecked Exception** | `extends RuntimeException` | ❌ **No** | Programming bugs and precondition violations (e.g. `IllegalArgumentException`). |
| **Custom Domain Exception** | `extends RuntimeException` | ❌ **No** | Expressive domain errors mapped to business rules (e.g. `CustomerNotPlusException`). |

---

## ❌ Bad Design (Swallowing Exceptions & Cryptic Errors)

```java
class BadPaymentService {
    public void processPayment(String userId, double amount) {
        // ❌ No input validation (Missing Fail-Fast)
        try {
            paymentGateway.charge(userId, amount);
        } catch (Exception e) {
            // ❌ SWALLOWING EXCEPTION: Silently ignores error, customer is never notified!
            System.out.println("Something went wrong"); 
        }
    }
}
```

### What is wrong?
- ⚠️ **Swallowing Exceptions:** Catching `Exception` and doing nothing hides critical production outages.
- ⚠️ **Cryptic Error Messages:** Vague strings like *"Something went wrong"* frustrate users and cause cart abandonment.
- ⚠️ **Missing Fail-Fast Checks:** Invalid negative amounts or null IDs proceed into downstream payment networks.

---

## ✅ Good Design (Fail-Fast Validation, Custom Exceptions, & Fail-Safe Fallbacks)

```java
// 1. Expressive Custom Domain Exception
class CustomerNotPlusException extends RuntimeException {
    public CustomerNotPlusException(String userId) {
        super("User #" + userId + " does not possess an active TUF+ Subscription.");
    }
}

// 2. Resilient Service with Fail-Fast Validation and Fallback Strategy
class CourseEnrollmentService {
    private final PaymentGateway paymentGateway;
    private final FallbackCatalogService fallbackCatalog;

    public CourseEnrollmentService(PaymentGateway gateway, FallbackCatalogService fallback) {
        this.paymentGateway = gateway;
        this.fallbackCatalog = fallback;
    }

    // Fail-Fast Transaction
    public void enrollInCourse(String userId, String courseId) {
        if (userId == null || courseId == null) {
            throw new IllegalArgumentException("User ID and Course ID must not be null."); // Fail-Fast
        }

        if (!isPlusMember(userId)) {
            throw new CustomerNotPlusException(userId); // Custom Domain Error
        }

        paymentGateway.charge(userId, 0.0);
        System.out.println("✅ Successfully enrolled User #" + userId + " into " + courseId);
    }

    // Fail-Safe Catalog Recommendation
    public List<String> getRecommendations(String userId) {
        try {
            return recommendationEngine.fetchRealtime(userId);
        } catch (Exception e) {
            System.err.println("⚠️ Real-time recommendation service unavailable. Falling back to cached popular courses.");
            return fallbackCatalog.getCachedPopularCourses(); // Graceful degradation
        }
    }
}
```

### Why it better demonstrates the concept:
- ✅ **Fail-Fast Integrity:** Validates inputs before executing downstream dependencies.
- ✅ **Expressive Error Signaling:** `CustomerNotPlusException` enables upstream UI controllers to return HTTP 403 Forbidden with exact instructions.
- ✅ **Graceful Degradation:** Fail-safe recommendation catches transient service drops and returns cached fallbacks.

---

## Java Classes

- **`CustomerNotPlusException`:** Domain-specific runtime exception representing subscription authorization failures.
- **`PaymentDeclinedException`:** Domain-specific checked exception representing recoverable banking card failures.
- **`OrderCheckoutService`:** Core business service demonstrating Fail-Fast parameter validation and Fail-Safe recommendation fallbacks.
- **`ExceptionHandlingLLDExample` (Main Driver):** Tests and validates happy paths, fail-fast validations, custom exceptions, and fail-safe recovery.

---

## How It Works

1. **Parameter Inspection (Fail-Fast):** Methods inspect arguments at the boundary and throw `IllegalArgumentException` on null/invalid inputs.
2. **Business Rule Enforcement:** If a non-Plus customer attempts to stream VIP content, `CustomerNotPlusException` is thrown.
3. **Graceful Fallback (Fail-Safe):** Non-critical operations (such as banner rendering) wrap downstream calls in `try-catch` and return default placeholders upon failure.

---

## When to Use

- **Financial & Payment Processing (Fail-Fast):** Never proceed on unvalidated amounts or failed card validations.
- **Content Delivery & Recommendation Engines (Fail-Safe):** Degrade to cached data if real-time microservices are unreachable.
- **Domain API Contracts (Custom Exceptions):** Expose clear domain errors (`UserNotFoundException`, `InsufficientStockException`) rather than generic `500 Internal Server Error`.

---

## When NOT to Use

- **Overusing Checked Exceptions:** Forcing callers to catch dozens of checked exceptions clutters code. Prefer unchecked domain exceptions for business logic.
- **Exceptions for Control Flow:** Never use `try-catch` blocks to replace standard `if-else` branching (it incurs heavy JVM stack-trace generation overhead).

---

## LLD Takeaway

Robust exception handling is essential for building **Resilient Microservices**, **Payment Gateways**, **E-Commerce Checkout Workflows**, and **Enterprise API Gateways** in Low-Level Design.

---

## 🎯 Quick Summary

- **Core Idea:** Manage system failures using Fail-Fast validation for data integrity, Fail-Safe fallbacks for availability, and Custom Exceptions for expressive domain modeling.
- **Code Demonstrates:** Validating checkout parameters (Fail-Fast), handling `CustomerNotPlusException` (Custom Exception), and serving cached courses on engine failure (Fail-Safe).
- **LLD Takeaway:** Never swallow exceptions; fail fast on invalid inputs and degrade gracefully on non-critical dependencies.
- **Memorable Rule:** *"Fail fast on core transactions; fail safe on auxiliary features; never swallow exceptions."*
