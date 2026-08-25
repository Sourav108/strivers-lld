# 02 - Building Resilient Systems

## Core Idea

**System Resilience** is the architectural capability of a system to absorb partial component failures, isolate faults, degrade gracefully, and recover automatically without suffering full outages. Instead of assuming zero failures, resilient systems treat network drops, service timeouts, and downstream crashes as inevitable invariants, neutralizing them through **Timeouts**, **Exponential Backoff Retries**, **Circuit Breakers**, **Active Failovers**, and **Cached Fallbacks**.

---

## 💡 Real-Life Analogy

### 🚅 The High-Speed Train & City Grid
- **Brittle System:** If a single track sensor malfunctions, the entire railway network across all 10 cities freezes completely.
- **Resilient System:** The train switches to a backup signaling frequency, slows down by 10% (Graceful Degradation), and reroutes through an alternate track (Failover) while notifying the maintenance dispatch team (Fault Isolation).

---

## 🛡️ Core Resilience Patterns

```
                                  +---------------------------------------+
                                  |         CLIENT INCOMING REQUEST       |
                                  +---------------------------------------+
                                                      |
                                                      v
                                        +---------------------------+
                                        |    1. TIMEOUT (Max 2s)    |
                                        +---------------------------+
                                                      |
                                                      v
                                        +---------------------------+
                                        |  2. CIRCUIT BREAKER       |
                                        |  [CLOSED / OPEN / HALF]   |
                                        +---------------------------+
                                          /                       \
                       Circuit CLOSED   /                           \  Circuit OPEN
                                      v                               v
                         +-----------------------+      +---------------------------+
                         | 3. PRIMARY SERVICE    |      | 4. FALLBACK / FAILOVER    |
                         | (e.g., Razorpay)      |      | (e.g., Stripe / Cache)    |
                         +-----------------------+      +---------------------------+
                                      |
                           Failed with transient error
                                      |
                                      v
                         +-----------------------+
                         | 5. EXPONENTIAL BACKOFF|
                         | RETRY (1s, 2s, 4s)    |
                         +-----------------------+
```

---

## ⚖️ Circuit Breaker State Transitions

```
                    +--------------------------------------------+
                    |                                            |
                    v                                            |
         +--------------------+   Failure Rate > Threshold   +--------------------+
         |       CLOSED       | ---------------------------> |        OPEN        |
         |  (Normal Traffic)  |                              | (Fast-Fail Calls)  |
         +--------------------+                              +--------------------+
                    ^                                                  |
                    | Trial Probe                                      | Wait Duration
                    | Succeeded                                        | Expired (10s)
                    |                                                  v
                    |                                        +--------------------+
                    +--------------------------------------- |     HALF-OPEN      |
                           Trial Probe Failed                |   (Test Probes)    |
                           (Re-Open Circuit)                 +--------------------+
```

| State | Behavior | Downstream Invoked? | Next Transition |
|---|---|---|---|
| **CLOSED** | System healthy; passes all requests. | ✅ Yes | Transitions to **OPEN** if failure rate $> 50\%$. |
| **OPEN** | Downstream unhealthy; fast-fails immediately to fallback. | ❌ No (Saves threads/resources) | Transitions to **HALF-OPEN** after cooldown timer (e.g. 10s). |
| **HALF-OPEN** | Sends limited test probe requests to test recovery. | 🟡 Limited probes | **CLOSED** if probes succeed; **OPEN** if probes fail. |

---

## ❌ Bad Design (Brittle Cascading Failure Anti-Pattern)

```java
class BrittleCheckoutService {
    // ❌ Infinite wait, naive tight-loop retries, no fallback, no failover!
    public void checkout(Order order) {
        while (true) {
            try {
                // 1. Blocks thread indefinitely if bank hangs!
                paymentService.charge(order); 
                break;
            } catch (Exception e) {
                // 2. Naive retry tight loop: DDoS hammers the already struggling bank API!
            }
        }
    }
}
```

### What is wrong?
- ⚠️ **Thread Exhaustion:** Unbounded network calls block HTTP worker threads indefinitely, taking down the entire web server.
- ⚠️ **Self-Inflicted DDoS (Thundering Herd):** Thousands of clients retrying simultaneously without delays crash downstream dependencies.
- ⚠️ **Zero Fault Isolation:** A failure in payment recommendations completely halts the ability to complete a checkout.

---

## ✅ Good Design (Resilient Architecture with Circuit Breaker, Failover, & Backoff)

```java
public class ResilientPaymentProcessor {
    private final CircuitBreaker circuitBreaker = new CircuitBreaker(3, 5000); // 3 failures -> Open for 5s
    private final PaymentGateway primaryGateway = new RazorpayGateway();
    private final PaymentGateway secondaryGateway = new StripeGateway();

    public boolean processPayment(String user, double amount) {
        // 1. Check Circuit Breaker before calling Primary
        if (circuitBreaker.allowRequest()) {
            try {
                boolean success = executeWithBackoff(() -> primaryGateway.charge(user, amount), 3, 500);
                if (success) {
                    circuitBreaker.recordSuccess();
                    return true;
                }
            } catch (Exception e) {
                circuitBreaker.recordFailure();
                System.err.println("⚠️ Primary Gateway failed. Tripping Circuit Breaker.");
            }
        }

        // 2. Automatic Failover to Standby Gateway
        System.out.println("🔄 [Failover Triggered] Routing transaction to Secondary Gateway (Stripe)...");
        return secondaryGateway.charge(user, amount);
    }

    private boolean executeWithBackoff(Callable<Boolean> task, int maxRetries, long baseDelayMs) {
        long delay = baseDelayMs;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                if (attempt == maxRetries) throw new RuntimeException(e);
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
                delay *= 2; // Exponential Backoff
            }
        }
        return false;
    }
}
```

---

## 📋 Resiliency Engineering Checklist

| Scenario | Symptom / Failure Mode | Recommended Resilience Pattern |
|---|---|---|
| **Temporary Traffic Burst** | Transient 503 / packet drops | **Exponential Backoff with Jitter** |
| **Persistent Outage** | Downstream service dead / crashing | **Circuit Breaker Pattern** |
| **Slow Third-Party API** | Thread pool starvation / hung socket | **Strict Bounded Timeouts (e.g. 2s)** |
| **Non-Critical Outage** | Recommendation / catalog API down | **Cached Stale Fallback / Fallback UI** |
| **Mission-Critical API Drop** | Primary Payment Processor down | **Active-Standby Gateway Failover** |

---

## Java Classes

- **`CircuitBreaker`:** Implements state transitions (`CLOSED`, `OPEN`, `HALF_OPEN`), failure thresholds, and recovery cooldown timers.
- **`PaymentGateway` (Interface):** Defines the payment execution contract.
- **`RazorpayPrimaryGateway` & `StripeSecondaryGateway`:** Concrete gateways demonstrating active-standby failover.
- **`RecommendationService`:** Demonstrates graceful fallback to local cache when live API fails.
- **`ResilientSystemsExample` (Main Driver):** Tests and validates backoff retries, circuit breaker state transitions, failover routing, and cached fallbacks.

---

## How It Works

1. Client executes a payment. The system checks `circuitBreaker.allowRequest()`.
2. If closed, it attempts to charge via `RazorpayPrimaryGateway` with exponential backoff retries.
3. If primary calls consistently fail, the circuit breaker trips to **OPEN**, short-circuiting future calls to save CPU and thread resources.
4. The system transparently engages **Failover** to `StripeSecondaryGateway` to complete customer checkouts.
5. Non-critical features (recommendations) gracefully degrade to local in-memory caches.

---

## When to Use

- **High-Volume Microservices:** E-commerce checkouts, payment processing, ride-hailing dispatchers.
- **Third-Party API Integrations:** SMS gateways, KYC verification APIs, Cloud storage providers.
- **Mission-Critical Backends:** Healthcare records, financial order-matching engines, airline reservation systems.

---

## When NOT to Use

- **Idempotency-Unsafe Operations without Request IDs:** Never blindly retry non-idempotent endpoints without unique transaction idempotency keys (risk of double charges).
- **Internal In-Memory Method Calls:** Circuit breakers add unnecessary overhead when calling simple local helper methods.

---

## LLD Takeaway

Building resilient systems is a key evaluation criterion in Low-Level Design interviews. Demonstrating fluency with **Circuit Breakers**, **Bounded Timeouts**, **Exponential Backoff Retries**, and **Active Failovers** proves production-grade system architecture expertise.

---

## 🎯 Quick Summary

- **Core Idea:** Resilient systems absorb partial failures without crashing through Timeouts, Exponential Backoff, Circuit Breakers, and Failovers.
- **Code Demonstrates:** A complete Circuit Breaker state machine (Closed/Open/Half-Open), exponential backoff retries, and automatic failover between payment providers.
- **LLD Takeaway:** Always protect external network calls with explicit timeouts, exponential backoff, and circuit breaker fallbacks.
- **Memorable Rule:** *"Expect failures; isolate faults with circuit breakers, retry with backoff, and failover gracefully."*
