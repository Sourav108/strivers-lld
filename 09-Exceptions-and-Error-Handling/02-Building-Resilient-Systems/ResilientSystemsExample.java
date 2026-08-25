import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Exceptions and Error Handling: Building Resilient Systems
 * 
 * Demonstrates:
 * 1. Circuit Breaker Pattern (CLOSED -> OPEN -> HALF-OPEN State Machine)
 * 2. Exponential Backoff Retry Strategy (Preventing Self-Inflicted Retry DDoS)
 * 3. Active-Standby Failover (Primary Gateway -> Secondary Gateway)
 * 4. Graceful Degradation (Cached Recommendations Fallback)
 * 5. Bounded Timeouts & Fault Isolation
 */

public class ResilientSystemsExample {

    // =========================================================================
    // 1. CIRCUIT BREAKER PATTERN IMPLEMENTATION
    // =========================================================================

    enum CircuitState { CLOSED, OPEN, HALF_OPEN }

    static class SimpleCircuitBreaker {
        private final String name;
        private final int failureThreshold;
        private final long cooldownPeriodMs;

        private CircuitState state = CircuitState.CLOSED;
        private int failureCount = 0;
        private long lastStateChangeTimestamp = 0;

        public SimpleCircuitBreaker(String name, int failureThreshold, long cooldownPeriodMs) {
            this.name = name;
            this.failureThreshold = failureThreshold;
            this.cooldownPeriodMs = cooldownPeriodMs;
        }

        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();

            if (state == CircuitState.OPEN) {
                if (now - lastStateChangeTimestamp >= cooldownPeriodMs) {
                    System.out.println("   🟡 [CircuitBreaker: " + name + "] Cooldown elapsed. Transitioning to HALF-OPEN (Testing single trial probe)...");
                    state = CircuitState.HALF_OPEN;
                    return true;
                }
                return false; // Fast-fail immediately
            }
            return true; // CLOSED or HALF_OPEN allow requests
        }

        public synchronized void recordSuccess() {
            if (state == CircuitState.HALF_OPEN) {
                System.out.println("   🟢 [CircuitBreaker: " + name + "] Trial probe SUCCEEDED! Transitioning back to CLOSED (Healthy).");
            }
            failureCount = 0;
            state = CircuitState.CLOSED;
        }

        public synchronized void recordFailure() {
            failureCount++;
            lastStateChangeTimestamp = System.currentTimeMillis();

            if (state == CircuitState.HALF_OPEN) {
                System.out.println("   🔴 [CircuitBreaker: " + name + "] Trial probe FAILED! Re-opening circuit (OPEN).");
                state = CircuitState.OPEN;
            } else if (failureCount >= failureThreshold) {
                System.out.println("   🔴 [CircuitBreaker: " + name + "] Threshold breached (" + failureCount + 
                                   " consecutive failures)! Tripping to OPEN (Fast-failing traffic).");
                state = CircuitState.OPEN;
            }
        }

        public CircuitState getState() { return state; }
        public String getName() { return name; }
    }

    // =========================================================================
    // 2. EXPONENTIAL BACKOFF RETRY EXECUTOR
    // =========================================================================

    static class RetryExecutor {
        public static <T> T executeWithExponentialBackoff(String taskName, Callable<T> task, int maxAttempts, long baseDelayMs) throws Exception {
            long delay = baseDelayMs;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    return task.call();
                } catch (Exception e) {
                    System.out.println("      ⚠️ [" + taskName + "] Attempt #" + attempt + " failed: " + e.getMessage());
                    if (attempt == maxAttempts) {
                        throw e; // Exhausted all attempts
                    }
                    System.out.println("      ⏳ Waiting " + delay + "ms (Exponential Backoff) before retry #" + (attempt + 1) + "...");
                    Thread.sleep(delay);
                    delay *= 2; // Double delay on each failure
                }
            }
            throw new RuntimeException("Retry attempts exhausted.");
        }
    }

    // =========================================================================
    // 3. PAYMENT GATEWAY CONTRACT & CONCRETE IMPLEMENTATIONS (Failover)
    // =========================================================================

    interface PaymentGateway {
        boolean charge(String user, double amount) throws Exception;
        String getProviderName();
    }

    // Primary Gateway: Outage simulation
    static class RazorpayPrimaryGateway implements PaymentGateway {
        private boolean simulateDown = true;

        public void setSimulateDown(boolean simulateDown) { this.simulateDown = simulateDown; }

        @Override
        public boolean charge(String user, double amount) throws Exception {
            if (simulateDown) {
                throw new RuntimeException("HTTP 504 Gateway Timeout: Bank Core Engine Unresponsive");
            }
            System.out.println("      💳 [Razorpay Primary] Payment of ₹" + amount + " processed successfully for " + user);
            return true;
        }

        @Override public String getProviderName() { return "Razorpay-Primary"; }
    }

    // Standby Secondary Gateway: Reliable Failover
    static class StripeSecondaryGateway implements PaymentGateway {
        @Override
        public boolean charge(String user, double amount) {
            System.out.println("      💳 [Stripe Standby Failover] Payment of ₹" + amount + " processed successfully for " + user);
            return true;
        }

        @Override public String getProviderName() { return "Stripe-Standby"; }
    }

    // =========================================================================
    // 4. RESILIENT PAYMENT ORCHESTRATOR
    // =========================================================================

    static class ResilientPaymentService {
        private final SimpleCircuitBreaker circuitBreaker = new SimpleCircuitBreaker("RazorpayCircuit", 2, 1000);
        private final RazorpayPrimaryGateway primaryGateway;
        private final StripeSecondaryGateway secondaryGateway;

        public ResilientPaymentService(RazorpayPrimaryGateway primary, StripeSecondaryGateway secondary) {
            this.primaryGateway = primary;
            this.secondaryGateway = secondary;
        }

        public boolean processPayment(String user, double amount) {
            System.out.println("\n💳 [Checkout] Initiating transaction for " + user + " (₹" + amount + ")...");

            // 1. Check Circuit Breaker before calling Primary
            if (circuitBreaker.allowRequest()) {
                try {
                    System.out.println("   🔵 [Routing] Attempting Primary Gateway (" + primaryGateway.getProviderName() + ")...");
                    boolean success = RetryExecutor.executeWithExponentialBackoff(
                        "Razorpay-Charge",
                        () -> primaryGateway.charge(user, amount),
                        2, 200 // Max 2 attempts, 200ms base delay
                    );
                    circuitBreaker.recordSuccess();
                    return success;
                } catch (Exception e) {
                    circuitBreaker.recordFailure();
                    System.out.println("   ⚠️ [Primary Failed] Primary gateway unavailable. Triggering Failover...");
                }
            } else {
                System.out.println("   🔴 [Fast-Fail] Circuit Breaker is OPEN! Skipping primary gateway to save resources.");
            }

            // 2. Automatic Failover to Standby Gateway
            System.out.println("   🔄 [Active Failover] Routing transaction to Secondary Gateway (" + secondaryGateway.getProviderName() + ")...");
            return secondaryGateway.charge(user, amount);
        }
    }

    // =========================================================================
    // 5. RESILIENT RECOMMENDATION SERVICE (Graceful Degradation via Cache)
    // =========================================================================

    static class ResilientRecommendationService {
        private final List<String> cachedCatalog = Arrays.asList(
            "TUF A2Z DSA Sheet", "Striver LLD Masterclass", "Core Java Concurrency"
        );

        public List<String> getRecommendations(String user) {
            try {
                // Simulate calling a live AI Recommendation Service that is down
                throw new RuntimeException("AI Recommendation Microservice Unreachable");
            } catch (Exception e) {
                // Graceful Degradation: return cached data
                System.out.println("   ⚠️ [Graceful Degradation] Live recommendations down. Serving cached popular catalog.");
                return cachedCatalog;
            }
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 🛡️ Building Resilient Systems: Architectural Demo ===");

        RazorpayPrimaryGateway primary = new RazorpayPrimaryGateway();
        StripeSecondaryGateway standby = new StripeSecondaryGateway();
        ResilientPaymentService paymentService = new ResilientPaymentService(primary, standby);
        ResilientRecommendationService recService = new ResilientRecommendationService();

        // --- Demo 1: Graceful Cache Degradation ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ DEMO 1: Graceful Degradation (Cached Fallback)");
        System.out.println("-----------------------------------------------------------");
        List<String> recommendations = recService.getRecommendations("sourav@takeuforward.org");
        System.out.println("   📚 Recommended Items Displayed to User: " + recommendations);

        // --- Demo 2: Primary Gateway Failure -> Retries -> Tripping Circuit Breaker -> Failover ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ DEMO 2: Exponential Backoff Retries & Active Failover");
        System.out.println("-----------------------------------------------------------");
        // Transaction 1: Primary fails after 2 retries, failover completes payment
        paymentService.processPayment("User-Alice", 1999.0);

        // Transaction 2: Circuit Breaker trips to OPEN on second consecutive failure
        paymentService.processPayment("User-Bob", 2499.0);

        // Transaction 3: Circuit Breaker is OPEN -> Immediately skips Primary and failovers to Stripe
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ DEMO 3: Circuit Breaker Fast-Fails Open State");
        System.out.println("-----------------------------------------------------------");
        paymentService.processPayment("User-Charlie", 3499.0);

        // --- Demo 4: Cooldown Recovery & Half-Open State ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ DEMO 4: Circuit Breaker Recovery (HALF-OPEN -> CLOSED)");
        System.out.println("-----------------------------------------------------------");
        System.out.println("⏳ Waiting 1.2s for Circuit Breaker Cooldown to expire...");
        Thread.sleep(1200);

        // Primary service recovers!
        primary.setSimulateDown(false);
        System.out.println("🔧 [Maintenance Complete] Razorpay Primary Gateway has recovered.");

        // Transaction 4: Probe request in HALF-OPEN state succeeds and closes circuit!
        paymentService.processPayment("User-David", 4999.0);

        System.out.println("\n===========================================================");
        System.out.println("🎯 Resilience Architecture (Circuit Breaker, Backoff, Failover) Verified!");
        System.out.println("===========================================================");
    }
}
