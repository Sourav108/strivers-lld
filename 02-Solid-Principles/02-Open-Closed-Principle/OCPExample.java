/**
 * SOLID Principles: Open/Closed Principle (OCP)
 * 
 * Core Concept: Software entities (classes, modules, functions, etc.) should be 
 * OPEN for extension, but CLOSED for modification.
 * 
 * You should be able to introduce new functionality without modifying existing, tested code.
 */

// ==========================================
// ❌ BAD DESIGN (Violates OCP)
// Every time a new country/region tax rule is added,
// calculateTotal() in InvoiceProcessor MUST be modified.
// ==========================================
class BadInvoiceProcessor {
    public double calculateTotal(String region, double amount) {
        if (region.equalsIgnoreCase("India")) {
            return amount + amount * 0.18; // 18% GST
        } else if (region.equalsIgnoreCase("US")) {
            return amount + amount * 0.08; // 8% Sales Tax
        } else if (region.equalsIgnoreCase("UK")) {
            return amount + amount * 0.12; // 12% VAT
        } else {
            return amount; // No tax for unknown region
        }
    }
}

// ==========================================
// ✅ GOOD DESIGN (Adheres to OCP)
// Core invoice processing is CLOSED for modification,
// but OPEN for extension by supplying new TaxCalculator implementations.
// ==========================================

// Step 1: Strategy Interface defining the contract
interface TaxCalculator {
    double calculateTax(double amount);
}

// Step 2: Concrete Strategy implementations for specific regions
class IndiaTaxCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.18; // 18% GST
    }
}

class USTaxCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.08; // 8% Sales Tax
    }
}

class UKTaxCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.12; // 12% VAT
    }
}

// Step 3: Core Invoice class decoupled from specific tax strategies (Dependency Injection)
class Invoice {
    private final double amount;
    private final TaxCalculator taxCalculator;

    public Invoice(double amount, TaxCalculator taxCalculator) {
        this.amount = amount;
        this.taxCalculator = taxCalculator;
    }

    public double getTotalAmount() {
        return amount + taxCalculator.calculateTax(amount);
    }
}

// Step 4: Extending for a new region (e.g. Germany 15%)
// Notice: We did NOT modify Invoice or any existing TaxCalculator!
class GermanyTaxCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.15; // 15% VAT
    }
}

// ==========================================
// 🚀 Main Driver Program
// ==========================================
public class OCPExample {
    public static void main(String[] args) {
        double baseAmount = 1000.0;

        System.out.println("=== ❌ Running Bad Design (Violates OCP) ===");
        BadInvoiceProcessor badProcessor = new BadInvoiceProcessor();
        System.out.println("Bad Total (India): " + badProcessor.calculateTotal("India", baseAmount));
        System.out.println("Bad Total (US):    " + badProcessor.calculateTotal("US", baseAmount));
        System.out.println("Bad Total (UK):    " + badProcessor.calculateTotal("UK", baseAmount));

        System.out.println("\n=== ✅ Running Good Design (Follows OCP) ===");
        
        Invoice indiaInvoice = new Invoice(baseAmount, new IndiaTaxCalculator());
        System.out.println("India Invoice Total: ₹" + indiaInvoice.getTotalAmount());

        Invoice usInvoice = new Invoice(baseAmount, new USTaxCalculator());
        System.out.println("US Invoice Total:    $" + usInvoice.getTotalAmount());

        Invoice ukInvoice = new Invoice(baseAmount, new UKTaxCalculator());
        System.out.println("UK Invoice Total:    £" + ukInvoice.getTotalAmount());

        // Adding Germany without changing existing classes
        Invoice germanyInvoice = new Invoice(baseAmount, new GermanyTaxCalculator());
        System.out.println("Germany Invoice Total (New Extension): €" + germanyInvoice.getTotalAmount());
    }
}
