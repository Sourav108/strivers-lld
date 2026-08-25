/**
 * GoF Design Patterns: Overview of 3 Core Categories
 * 
 * 1. Creational: Focuses on object creation (e.g. Factory Pattern)
 * 2. Structural: Focuses on object composition & interface compatibility (e.g. Adapter Pattern)
 * 3. Behavioral: Focuses on object communication & responsibilities (e.g. Strategy Pattern)
 */

// =========================================================================
// 1. CREATIONAL PATTERN EXAMPLE (Beverage Factory / Vending Machine)
// Decouples the client from direct object instantiation details.
// =========================================================================

interface Beverage {
    void drink();
}

class Coffee implements Beverage {
    @Override
    public void drink() {
        System.out.println("☕ [Creational: Factory] Sipping hot freshly brewed Coffee.");
    }
}

class Juice implements Beverage {
    @Override
    public void drink() {
        System.out.println("🧃 [Creational: Factory] Drinking chilled Orange Juice.");
    }
}

class BeverageFactory {
    public static Beverage createBeverage(String type) {
        if ("coffee".equalsIgnoreCase(type)) {
            return new Coffee();
        } else if ("juice".equalsIgnoreCase(type)) {
            return new Juice();
        }
        throw new IllegalArgumentException("Unknown beverage type: " + type);
    }
}

// =========================================================================
// 2. STRUCTURAL PATTERN EXAMPLE (USB-C to Micro-USB Adapter)
// Bridges two incompatible interfaces so they can collaborate.
// =========================================================================

// Target interface expected by modern smartphone
interface UsbCTarget {
    void chargeWithUsbC();
}

// Adaptee: Legacy hardware that only provides Micro-USB charging
class LegacyMicroUsbCharger {
    public void chargeWithMicroUsb() {
        System.out.println("⚡ [Structural: Adapter] Charging using 5V legacy Micro-USB current.");
    }
}

// Adapter: Makes LegacyMicroUsbCharger compatible with UsbCTarget
class UsbCToMicroUsbAdapter implements UsbCTarget {
    private final LegacyMicroUsbCharger legacyCharger;

    public UsbCToMicroUsbAdapter(LegacyMicroUsbCharger legacyCharger) {
        this.legacyCharger = legacyCharger;
    }

    @Override
    public void chargeWithUsbC() {
        System.out.print("🔌 [Adapter Bridging] ");
        legacyCharger.chargeWithMicroUsb();
    }
}

// =========================================================================
// 3. BEHAVIORAL PATTERN EXAMPLE (Payment Strategy)
// Decouples the algorithm / execution responsibility from the caller.
// =========================================================================

interface PaymentStrategy {
    void pay(double amount);
}

class UpiPayment implements PaymentStrategy {
    private final String upiId;
    public UpiPayment(String upiId) { this.upiId = upiId; }

    @Override
    public void pay(double amount) {
        System.out.println("📱 [Behavioral: Strategy] Paid ₹" + amount + " via UPI ID: " + upiId);
    }
}

class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CreditCardPayment(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public void pay(double amount) {
        System.out.println("💳 [Behavioral: Strategy] Paid ₹" + amount + " via Card: " + cardNumber);
    }
}

class CheckoutCart {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy not set!");
        }
        paymentStrategy.pay(amount);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class DesignPatternsOverviewExample {
    public static void main(String[] args) {
        System.out.println("=== 1. CREATIONAL PATTERN (Factory Method) ===");
        // Client requests beverage without knowing instantiation internals
        Beverage coffee = BeverageFactory.createBeverage("coffee");
        coffee.drink();

        Beverage juice = BeverageFactory.createBeverage("juice");
        juice.drink();

        System.out.println("\n=== 2. STRUCTURAL PATTERN (Adapter Pattern) ===");
        // Phone requires USB-C, but we only have a legacy Micro-USB charger
        LegacyMicroUsbCharger legacyCharger = new LegacyMicroUsbCharger();
        UsbCTarget adaptedCharger = new UsbCToMicroUsbAdapter(legacyCharger);
        adaptedCharger.chargeWithUsbC();

        System.out.println("\n=== 3. BEHAVIORAL PATTERN (Strategy Pattern) ===");
        // Cart delegates payment processing to interchangeable behavioral strategies
        CheckoutCart cart = new CheckoutCart();

        cart.setPaymentStrategy(new UpiPayment("sourav@oksbi"));
        cart.checkout(1499.00);

        cart.setPaymentStrategy(new CreditCardPayment("4111-XXXX-XXXX-8899"));
        cart.checkout(4999.00);
    }
}
