/**
 * DRY Principle: Don't Repeat Yourself
 * 
 * Core Concept: Every piece of business logic or calculation must have a single,
 * authoritative source of truth in the system.
 */

// ==========================================
// ❌ BAD DESIGN (Violates DRY)
// ==========================================
class BadCartService {
    public double calculateTotal(double price, int quantity) {
        double subtotal = price * quantity;
        double tax = subtotal * 0.18; // 18% GST duplicated
        return subtotal + tax;
    }
}

class BadInvoiceService {
    public double generateInvoiceTotal(double amount) {
        double tax = amount * 0.18; // 18% GST duplicated
        return amount + tax;
    }
}

// ==========================================
// ✅ GOOD DESIGN (Adheres to DRY)
// ==========================================
// Class 1: Single authoritative class for tax calculations.
class TaxCalculator {
    private static final double GST_RATE = 0.18;

    public static double calculateTax(double amount) {
        return amount * GST_RATE;
    }
}

// Class 2: Uses the centralized TaxCalculator.
class CartService {
    public double calculateTotal(double price, int quantity) {
        double subtotal = price * quantity;
        return subtotal + TaxCalculator.calculateTax(subtotal);
    }
}

// Class 3: Reuses the same centralized TaxCalculator.
class InvoiceService {
    public double generateInvoiceTotal(double amount) {
        return amount + TaxCalculator.calculateTax(amount);
    }
}

public class DRYExample {
    public static void main(String[] args) {
        CartService cart = new CartService();
        InvoiceService invoice = new InvoiceService();

        System.out.println("Cart Total: " + cart.calculateTotal(100.0, 2));
        System.out.println("Invoice Total: " + invoice.generateInvoiceTotal(200.0));
    }
}
