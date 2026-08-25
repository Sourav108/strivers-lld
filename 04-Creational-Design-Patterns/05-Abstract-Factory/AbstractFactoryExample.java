/**
 * Creational Design Patterns: Abstract Factory Pattern
 * 
 * Core Concept: Provides an interface for creating families of related or
 * dependent objects without specifying their concrete classes.
 * 
 * Guarantees cross-product compatibility (e.g., India Payment + GST Invoice).
 */

// =========================================================================
// 1. ABSTRACT PRODUCTS (Interfaces for the Product Family)
// =========================================================================

interface PaymentGateway {
    void processPayment(double amount);
}

interface Invoice {
    void generateInvoice();
}

// =========================================================================
// 2. CONCRETE PRODUCTS: INDIA FAMILY
// =========================================================================

class RazorpayGateway implements PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("🇮🇳 [Razorpay] Processed ₹" + amount + " via UPI/NetBanking.");
    }
}

class PayUGateway implements PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("🇮🇳 [PayU] Processed ₹" + amount + " via Card/Wallet.");
    }
}

class GSTInvoice implements Invoice {
    @Override
    public void generateInvoice() {
        System.out.println("📄 [GST Invoice] Generated Indian GST compliant tax invoice (18% GST).");
    }
}

// =========================================================================
// 3. CONCRETE PRODUCTS: US FAMILY
// =========================================================================

class PayPalGateway implements PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("🇺🇸 [PayPal] Processed $" + amount + " via PayPal Wallet.");
    }
}

class StripeGateway implements PaymentGateway {
    @Override
    public void processPayment(double amount) {
        System.out.println("🇺🇸 [Stripe] Processed $" + amount + " via International Credit Card.");
    }
}

class USInvoice implements Invoice {
    @Override
    public void generateInvoice() {
        System.out.println("📄 [US Invoice] Generated US Sales Tax compliant invoice.");
    }
}

// =========================================================================
// 4. ABSTRACT FACTORY INTERFACE
// =========================================================================

interface RegionFactory {
    PaymentGateway createPaymentGateway(String gatewayType);
    Invoice createInvoice();
}

// =========================================================================
// 5. CONCRETE FACTORIES (Producing specific product families)
// =========================================================================

class IndiaFactory implements RegionFactory {
    @Override
    public PaymentGateway createPaymentGateway(String gatewayType) {
        if ("razorpay".equalsIgnoreCase(gatewayType)) {
            return new RazorpayGateway();
        } else if ("payu".equalsIgnoreCase(gatewayType)) {
            return new PayUGateway();
        }
        throw new IllegalArgumentException("Unsupported India gateway: " + gatewayType);
    }

    @Override
    public Invoice createInvoice() {
        return new GSTInvoice();
    }
}

class USFactory implements RegionFactory {
    @Override
    public PaymentGateway createPaymentGateway(String gatewayType) {
        if ("paypal".equalsIgnoreCase(gatewayType)) {
            return new PayPalGateway();
        } else if ("stripe".equalsIgnoreCase(gatewayType)) {
            return new StripeGateway();
        }
        throw new IllegalArgumentException("Unsupported US gateway: " + gatewayType);
    }

    @Override
    public Invoice createInvoice() {
        return new USInvoice();
    }
}

// =========================================================================
// 6. CLIENT SERVICE (Decoupled from concrete product families)
// =========================================================================

class CheckoutService {
    private final PaymentGateway paymentGateway;
    private final Invoice invoice;

    public CheckoutService(RegionFactory factory, String gatewayType) {
        // Enforces that paymentGateway and invoice come from the EXACT SAME family
        this.paymentGateway = factory.createPaymentGateway(gatewayType);
        this.invoice = factory.createInvoice();
    }

    public void completeOrder(double amount) {
        paymentGateway.processPayment(amount);
        invoice.generateInvoice();
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class AbstractFactoryExample {
    public static void main(String[] args) {
        System.out.println("=== 1. Processing India Checkout (Razorpay + GST Invoice) ===");
        RegionFactory indiaFactory = new IndiaFactory();
        CheckoutService indiaCheckout = new CheckoutService(indiaFactory, "razorpay");
        indiaCheckout.completeOrder(2499.00);

        System.out.println("\n=== 2. Processing US Checkout (Stripe + US Sales Tax Invoice) ===");
        RegionFactory usFactory = new USFactory();
        CheckoutService usCheckout = new CheckoutService(usFactory, "stripe");
        usCheckout.completeOrder(49.99);
    }
}
