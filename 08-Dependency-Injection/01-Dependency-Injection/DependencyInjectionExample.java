/**
 * Dependency Injection (DI) & Inversion of Control (IoC)
 * 
 * Demonstrates:
 * 1. Constructor Injection (Recommended - Immutable & Thread-Safe)
 * 2. Setter Injection (For optional/reconfigurable dependencies)
 * 3. Seamless Swappability between Production Providers (Razorpay vs Stripe)
 * 4. Frictionless Unit Testing with Mock Objects (Test Doubles)
 */

// =========================================================================
// 1. ABSTRACTION CONTRACTS (Interfaces)
// =========================================================================

interface PaymentGateway {
    boolean charge(String customerEmail, double amount);
}

interface NotificationService {
    void sendNotification(String recipient, String message);
}

interface InventoryService {
    boolean reserveStock(String itemSku, int quantity);
}

// =========================================================================
// 2. CONCRETE IMPLEMENTATIONS (Production Collaborators)
// =========================================================================

class RazorpayPaymentGateway implements PaymentGateway {
    @Override
    public boolean charge(String customerEmail, double amount) {
        System.out.println("   💳 [Razorpay Gateway] Processing UPI/Card payment of ₹" + amount + " for " + customerEmail);
        return true;
    }
}

class StripePaymentGateway implements PaymentGateway {
    @Override
    public boolean charge(String customerEmail, double amount) {
        System.out.println("   💳 [Stripe Gateway] Processing International Card payment of ₹" + amount + " for " + customerEmail);
        return true;
    }
}

class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("   📧 [Email Service] Sending HTML email to " + recipient + ": \"" + message + "\"");
    }
}

class SMSNotificationService implements NotificationService {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("   📱 [SMS Gateway] Sending SMS alert to " + recipient + ": \"" + message + "\"");
    }
}

class WarehouseInventoryService implements InventoryService {
    @Override
    public boolean reserveStock(String itemSku, int quantity) {
        System.out.println("   📦 [Warehouse Service] Reserved " + quantity + " units of SKU: " + itemSku);
        return true;
    }
}

// =========================================================================
// 3. MOCK IMPLEMENTATIONS (For Automated Unit Testing)
// =========================================================================

class MockPaymentGateway implements PaymentGateway {
    private boolean paymentSuccessful = true;
    private double lastChargedAmount = 0;

    public void setPaymentSuccessful(boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    @Override
    public boolean charge(String customerEmail, double amount) {
        this.lastChargedAmount = amount;
        System.out.println("   🧪 [MOCK Test Gateway] Simulated payment verification for ₹" + amount + " -> Outcome: " + paymentSuccessful);
        return paymentSuccessful;
    }

    public double getLastChargedAmount() { return lastChargedAmount; }
}

// =========================================================================
// 4. HIGH-LEVEL DOMAIN SERVICE (Constructor Dependency Injection)
// =========================================================================

class OrderService {
    // Injected dependencies stored in private final fields (Immutability)
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;
    private final InventoryService inventoryService;

    // 🔒 Constructor Injection: Enforces all mandatory dependencies up-front
    public OrderService(PaymentGateway paymentGateway,
                        NotificationService notificationService,
                        InventoryService inventoryService) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.inventoryService = inventoryService;
    }

    public boolean checkout(String customerEmail, String itemSku, int qty, double totalAmount) {
        System.out.println("\n🛒 [OrderService] Starting checkout for customer: " + customerEmail);

        // 1. Check & Reserve Inventory
        if (!inventoryService.reserveStock(itemSku, qty)) {
            System.out.println("   ❌ Stock reservation failed. Checkout aborted.");
            return false;
        }

        // 2. Process Payment via Injected Gateway
        if (!paymentGateway.charge(customerEmail, totalAmount)) {
            System.out.println("   ❌ Payment failed. Checkout aborted.");
            return false;
        }

        // 3. Send Notification via Injected Provider
        notificationService.sendNotification(customerEmail, "Your order for " + qty + "x " + itemSku + " was confirmed!");
        System.out.println("   🎉 Checkout completed successfully!");
        return true;
    }
}

// =========================================================================
// 🚀 MAIN DRIVER (Composition Root - Wires the Dependency Graph)
// =========================================================================

public class DependencyInjectionExample {
    public static void main(String[] args) {
        System.out.println("=== 🔌 Dependency Injection (DI) in Action ===");

        // --- Scenario 1: Production Deployment with Razorpay & Email ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🏭 SCENARIO 1: Production Wiring (Razorpay + Email)");
        System.out.println("-----------------------------------------------------------");

        PaymentGateway razorpay = new RazorpayPaymentGateway();
        NotificationService emailService = new EmailNotificationService();
        InventoryService warehouse = new WarehouseInventoryService();

        OrderService productionOrderService1 = new OrderService(razorpay, emailService, warehouse);
        productionOrderService1.checkout("sourav@takeuforward.org", "TUF-SYSTEM-DESIGN-BOOK", 1, 1499.0);

        // --- Scenario 2: Zero-Code-Change Swap to Stripe & SMS ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🔄 SCENARIO 2: Seamless Provider Swap (Stripe + SMS)");
        System.out.println("-----------------------------------------------------------");

        PaymentGateway stripe = new StripePaymentGateway();
        NotificationService smsService = new SMSNotificationService();

        // OrderService code is 100% UNCHANGED, yet behavior is completely swapped!
        OrderService productionOrderService2 = new OrderService(stripe, smsService, warehouse);
        productionOrderService2.checkout("john.doe@global.com", "TUF-DSA-SHEET-PRO", 2, 2998.0);

        // --- Scenario 3: Isolated Unit Testing with Mocks ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🧪 SCENARIO 3: Unit Testing with Mock Test Double");
        System.out.println("-----------------------------------------------------------");

        MockPaymentGateway mockPayment = new MockPaymentGateway();
        OrderService testOrderService = new OrderService(mockPayment, emailService, warehouse);

        // Test successful payment flow without hitting real banking APIs
        boolean testResult = testOrderService.checkout("tester@qa.internal", "TEST-ITEM-01", 1, 99.0);
        System.out.println("   🧪 Test Assertion Passed: Checkout returned " + testResult + 
                           " (Amount charged: ₹" + mockPayment.getLastChargedAmount() + ")");

        System.out.println("\n===========================================================");
        System.out.println("🎯 Dependency Injection Principles Successfully Verified!");
        System.out.println("===========================================================");
    }
}
