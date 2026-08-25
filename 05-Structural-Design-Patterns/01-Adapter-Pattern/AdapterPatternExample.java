/**
 * Structural Design Patterns: Adapter Pattern
 * 
 * Core Concept: Converts the interface of an incompatible class (Adaptee)
 * into an interface expected by the client (Target), allowing them to collaborate.
 */

// =========================================================================
// ❌ BAD DESIGN (Without Adapter: Branching across incompatible APIs)
// =========================================================================

class BadCheckoutService {
    public void checkout(String provider, String orderId, double amount) {
        if ("PayU".equalsIgnoreCase(provider)) {
            PayUGateway payU = new PayUGateway();
            payU.pay(orderId, amount);
        } else if ("Razorpay".equalsIgnoreCase(provider)) {
            RazorpayAPI razorpay = new RazorpayAPI();
            // ❌ Incompatible method name and signature hardcoded into business logic!
            razorpay.makePayment(orderId, amount);
        } else {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}

// =========================================================================
// ✅ GOOD DESIGN (Adhering to Adapter Pattern)
// =========================================================================

// Step 1: Target Interface (Contract expected by the client application)
interface PaymentGateway {
    void pay(String orderId, double amount);
}

// Step 2: Conforming Concrete Implementation
class PayUGateway implements PaymentGateway {
    @Override
    public void pay(String orderId, double amount) {
        System.out.println("💳 [PayU Gateway] Successfully processed ₹" + amount + " for order #" + orderId);
    }
}

// Step 3: Adaptee (Third-party SDK with an incompatible interface)
class RazorpayAPI {
    // Incompatible method signature: makePayment() instead of pay()
    public void makePayment(String invoiceId, double amountInRupees) {
        System.out.println("⚡ [Razorpay API (Third-Party)] Processed payment of ₹" + amountInRupees + " for invoice #" + invoiceId);
    }
}

// Step 4: Adapter Class (Bridges Target Interface to Adaptee)
class RazorpayAdapter implements PaymentGateway {
    private final RazorpayAPI razorpayAPI;

    public RazorpayAdapter(RazorpayAPI razorpayAPI) {
        this.razorpayAPI = razorpayAPI;
    }

    @Override
    public void pay(String orderId, double amount) {
        // Translates target method call into Adaptee's specific method
        razorpayAPI.makePayment(orderId, amount);
    }
}

// Step 5: Client Service (Operates purely against the Target Interface)
class CheckoutService {
    private final PaymentGateway paymentGateway;

    // Dependency Injection
    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void checkout(String orderId, double amount) {
        paymentGateway.pay(orderId, amount);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class AdapterPatternExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Hardcoded Incompatible Calls ===");
        BadCheckoutService badCheckout = new BadCheckoutService();
        badCheckout.checkout("PayU", "ORD-101", 1200.0);
        badCheckout.checkout("Razorpay", "ORD-102", 2400.0);

        System.out.println("\n=== ✅ 2. Good Design: Decoupled via Adapter Pattern ===");

        // 1. Using standard PayU Gateway
        PaymentGateway payUGateway = new PayUGateway();
        CheckoutService payUCheckout = new CheckoutService(payUGateway);
        payUCheckout.checkout("ORD-201", 1780.0);

        // 2. Using Razorpay via RazorpayAdapter without modifying CheckoutService
        RazorpayAPI razorpayAPI = new RazorpayAPI();
        PaymentGateway razorpayAdapter = new RazorpayAdapter(razorpayAPI);
        CheckoutService razorpayCheckout = new CheckoutService(razorpayAdapter);
        razorpayCheckout.checkout("ORD-202", 3450.0);
    }
}
