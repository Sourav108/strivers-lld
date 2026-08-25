import java.util.ArrayList;
import java.util.List;

/**
 * Behavioural Design Patterns: Visitor Pattern
 * 
 * Core Concept: Allows adding new operations to existing class hierarchies
 * without modifying the classes themselves, leveraging Double Dispatch.
 */

// =========================================================================
// 1. VISITOR INTERFACE (Declares overloaded visit methods for each Element)
// =========================================================================

interface ItemVisitor {
    void visit(PhysicalProduct item);
    void visit(DigitalProduct item);
    void visit(GiftCard item);
}

// =========================================================================
// 2. ELEMENT INTERFACE (Declares accept method for Visitor)
// =========================================================================

interface Item {
    void accept(ItemVisitor visitor);
}

// =========================================================================
// 3. CONCRETE ELEMENTS (Domain Entities)
// =========================================================================

class PhysicalProduct implements Item {
    private final String name;
    private final double price;
    private final double weightInKg;

    public PhysicalProduct(String name, double price, double weightInKg) {
        this.name = name;
        this.price = price;
        this.weightInKg = weightInKg;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        // Double Dispatch: 1st dispatch resolves Item type; 2nd dispatch resolves visit(PhysicalProduct)
        visitor.visit(this);
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getWeightInKg() { return weightInKg; }
}

class DigitalProduct implements Item {
    private final String name;
    private final double price;
    private final int downloadSizeInMB;

    public DigitalProduct(String name, double price, int downloadSizeInMB) {
        this.name = name;
        this.price = price;
        this.downloadSizeInMB = downloadSizeInMB;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getDownloadSizeInMB() { return downloadSizeInMB; }
}

class GiftCard implements Item {
    private final String code;
    private final double balance;

    public GiftCard(String code, double balance) {
        this.code = code;
        this.balance = balance;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        visitor.visit(this);
    }

    public String getCode() { return code; }
    public double getBalance() { return balance; }
}

// =========================================================================
// 4. CONCRETE VISITORS (Encapsulating operations across all elements)
// =========================================================================

// Visitor 1: Invoice Generation Operation
class InvoiceVisitor implements ItemVisitor {
    @Override
    public void visit(PhysicalProduct item) {
        System.out.println("📄 [Invoice - Physical] '" + item.getName() + "' (₹" + item.getPrice() + ", Weight: " + item.getWeightInKg() + "kg) -> Physical shipment");
    }

    @Override
    public void visit(DigitalProduct item) {
        System.out.println("📄 [Invoice - Digital] '" + item.getName() + "' (₹" + item.getPrice() + ", Size: " + item.getDownloadSizeInMB() + "MB) -> Download link sent via email");
    }

    @Override
    public void visit(GiftCard item) {
        System.out.println("📄 [Invoice - GiftCard] Voucher Code: " + item.getCode() + " (Valued at ₹" + item.getBalance() + ") -> Activated digitally");
    }
}

// Visitor 2: Shipping Cost Calculation Operation
class ShippingCostVisitor implements ItemVisitor {
    @Override
    public void visit(PhysicalProduct item) {
        double cost = item.getWeightInKg() * 50.0;
        System.out.println("🚚 [Shipping Cost] '" + item.getName() + "': ₹" + cost + " (Based on " + item.getWeightInKg() + "kg cargo weight)");
    }

    @Override
    public void visit(DigitalProduct item) {
        System.out.println("⚡ [Shipping Cost] '" + item.getName() + "': ₹0.00 (Instant electronic delivery)");
    }

    @Override
    public void visit(GiftCard item) {
        System.out.println("⚡ [Shipping Cost] Gift Card '" + item.getCode() + "': ₹0.00 (Digital delivery)");
    }
}

// Visitor 3: Tax Audit Calculation Operation (Added without modifying any Item class!)
class GSTTaxVisitor implements ItemVisitor {
    @Override
    public void visit(PhysicalProduct item) {
        double gst = item.getPrice() * 0.18; // 18% standard GST
        System.out.println("🏛️ [GST Audit] '" + item.getName() + "' 18% Goods Tax: ₹" + gst);
    }

    @Override
    public void visit(DigitalProduct item) {
        double gst = item.getPrice() * 0.18; // 18% digital services tax
        System.out.println("🏛️ [GST Audit] '" + item.getName() + "' 18% Digital Service Tax: ₹" + gst);
    }

    @Override
    public void visit(GiftCard item) {
        System.out.println("🏛️ [GST Audit] Gift Card '" + item.getCode() + "' 0% Tax (Prepaid financial instrument)");
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class VisitorPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🛒 E-Commerce Cart Operations with Visitor Pattern ===");

        // 1. Build a heterogeneous shopping cart
        List<Item> cart = new ArrayList<>();
        cart.add(new PhysicalProduct("Mechanical Gaming Keyboard", 4500.0, 1.2));
        cart.add(new DigitalProduct("System Design Video Masterclass", 2499.0, 850));
        cart.add(new GiftCard("TUF-FESTIVE-1000", 1000.0));

        // 2. Instantiate Operations (Visitors)
        ItemVisitor invoiceVisitor = new InvoiceVisitor();
        ItemVisitor shippingVisitor = new ShippingCostVisitor();
        ItemVisitor taxVisitor = new GSTTaxVisitor();

        // 3. Execute Operations polymorphically via Double Dispatch
        System.out.println("\n--- 1. Generating Invoices ---");
        for (Item item : cart) {
            item.accept(invoiceVisitor);
        }

        System.out.println("\n--- 2. Calculating Shipping Freight ---");
        for (Item item : cart) {
            item.accept(shippingVisitor);
        }

        System.out.println("\n--- 3. Calculating GST Tax Breakdown ---");
        for (Item item : cart) {
            item.accept(taxVisitor);
        }
    }
}
