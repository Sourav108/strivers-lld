import java.util.Arrays;
import java.util.List;

/**
 * Exceptions and Error Handling in Low-Level Design (LLD)
 * 
 * Demonstrates:
 * 1. Fail-Fast Validation (Preventing Invalid State & Protecting Data Integrity)
 * 2. Fail-Safe Fallbacks (Graceful Degradation for Auxiliary Services)
 * 3. Checked Exceptions for Recoverable External I/O Failures (PaymentDeclinedException)
 * 4. Unchecked Custom Exceptions for Expressive Domain Modeling (CustomerNotPlusException)
 * 5. Safe Retry Logic for Transient Failures
 */

public class ExceptionHandlingLLDExample {

    // =========================================================================
    // 1. CUSTOM DOMAIN EXCEPTIONS
    // =========================================================================

    // Custom Unchecked Exception for Domain Business Rule Violation
    static class CustomerNotPlusException extends RuntimeException {
        public CustomerNotPlusException(String userId) {
            super("Access Denied: User '" + userId + "' does not possess an active TUF+ VIP Subscription.");
        }
    }

    // Custom Checked Exception for External Payment Failures (Recoverable)
    static class PaymentDeclinedException extends Exception {
        private final String errorCode;

        public PaymentDeclinedException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() { return errorCode; }
    }

    // =========================================================================
    // 2. DOMAIN SERVICES (Demonstrating Fail-Fast & Fail-Safe Patterns)
    // =========================================================================

    static class TUFCourseService {

        // --- A. FAIL-FAST STRATEGY ---
        // Halts immediately upon detecting invalid preconditions or business rule breaches
        public void accessVipCourse(String userId, String courseCode) {
            // 1. Fail-Fast on Input Boundary Validation
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("❌ [Fail-Fast Validation] User ID must not be null or blank.");
            }
            if (courseCode == null || courseCode.trim().isEmpty()) {
                throw new IllegalArgumentException("❌ [Fail-Fast Validation] Course Code must not be null or blank.");
            }

            // 2. Fail-Fast on Domain Authorization
            if (!isPlusCustomer(userId)) {
                throw new CustomerNotPlusException(userId);
            }

            System.out.println("   🔓 [Access Granted] User '" + userId + "' accessed VIP Course: " + courseCode);
        }

        // --- B. RECOVERABLE OPERATION WITH RETRY ---
        public void processCoursePayment(String userId, double amount) throws PaymentDeclinedException {
            if (amount <= 0) {
                throw new IllegalArgumentException("❌ [Fail-Fast Validation] Payment amount must be greater than 0.");
            }

            int attempts = 0;
            int maxRetries = 2;
            boolean paymentSuccess = false;

            while (!paymentSuccess && attempts <= maxRetries) {
                attempts++;
                System.out.println("   💳 [Payment Attempt #" + attempts + "] Charging ₹" + amount + " for user " + userId + "...");

                // Simulate simulated transient network drop on attempt 1, success on attempt 2
                if (attempts < 2) {
                    System.out.println("      ⚠️ Transient bank gateway timeout. Retrying in 200ms...");
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                } else {
                    paymentSuccess = true;
                    System.out.println("      ✅ Bank confirmed transaction ID: TXN-" + System.currentTimeMillis());
                }
            }

            if (!paymentSuccess) {
                throw new PaymentDeclinedException("Bank declined the transaction after retries.", "ERR_CARD_DECLINED");
            }
        }

        // --- C. FAIL-SAFE STRATEGY (Graceful Degradation) ---
        // Catches downstream failures and returns cached/default fallback without crashing
        public List<String> getRecommendedCourses(String userId) {
            try {
                // Simulate calling a flaky downstream AI Recommendation Service
                return callFlakyRecommendationEngine(userId);
            } catch (Exception e) {
                // Fail-safe: Log root cause and return fallback catalog
                System.err.println("   ⚠️ [Fail-Safe Notice] AI Recommendation engine offline (" + e.getMessage() + 
                                   "). Falling back to curated default catalog.");
                return getFallbackCatalog();
            }
        }

        private List<String> callFlakyRecommendationEngine(String userId) {
            // Simulate an unexpected downstream microservice timeout/failure
            throw new RuntimeException("HTTP 503 Service Unavailable: AI Microservice Cluster Down");
        }

        private List<String> getFallbackCatalog() {
            return Arrays.asList("1. Striver's A2Z DSA Sheet", "2. Core Java Multithreading", "3. LLD System Design Masterclass");
        }

        private boolean isPlusCustomer(String userId) {
            // Mock check: Only 'raj_plus' and 'sourav_pro' are VIP users
            return "raj_plus".equalsIgnoreCase(userId) || "sourav_pro".equalsIgnoreCase(userId);
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 🛡️ Exception Handling & Resilient LLD Demonstration ===");
        TUFCourseService service = new TUFCourseService();

        // --- Test 1: Happy Path ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ TEST 1: Happy Path (Authorized Plus Member)");
        System.out.println("-----------------------------------------------------------");
        service.accessVipCourse("sourav_pro", "LLD-ADVANCED-CONCURRENCY");

        // --- Test 2: Fail-Fast Input Validation ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ TEST 2: Fail-Fast Boundary Validation (Null Parameter)");
        System.out.println("-----------------------------------------------------------");
        try {
            service.accessVipCourse(null, "LLD-CONCURRENCY");
        } catch (IllegalArgumentException e) {
            System.out.println("   Caught Expected Fail-Fast Error -> " + e.getMessage());
        }

        // --- Test 3: Custom Domain Exception ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ TEST 3: Custom Domain Business Exception (CustomerNotPlus)");
        System.out.println("-----------------------------------------------------------");
        try {
            service.accessVipCourse("guest_user_123", "LLD-ADVANCED-CONCURRENCY");
        } catch (CustomerNotPlusException e) {
            System.out.println("   Caught Domain Exception -> " + e.getMessage());
            System.out.println("   ℹ️ Action: Redirecting user to TUF+ Subscription Upgrade Page.");
        }

        // --- Test 4: Transient Error with Retry & Checked Exception Handling ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ TEST 4: Transient Error Recovery & Payment Checkout");
        System.out.println("-----------------------------------------------------------");
        try {
            service.processCoursePayment("sourav_pro", 4999.0);
        } catch (PaymentDeclinedException e) {
            System.out.println("   Payment Error: " + e.getMessage() + " (Code: " + e.getErrorCode() + ")");
        }

        // --- Test 5: Fail-Safe Graceful Degradation ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ TEST 5: Fail-Safe Fallback (Downstream Engine Failure)");
        System.out.println("-----------------------------------------------------------");
        List<String> recommendations = service.getRecommendedCourses("sourav_pro");
        System.out.println("   📚 Displaying Recommended Courses to User:");
        for (String course : recommendations) {
            System.out.println("      " + course);
        }

        System.out.println("\n===========================================================");
        System.out.println("🎯 Exception Handling & Fault Tolerance Verified Successfully!");
        System.out.println("===========================================================");
    }
}
