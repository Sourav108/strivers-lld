import java.util.ArrayList;
import java.util.List;

/**
 * Structural Design Patterns: Composite Pattern
 * 
 * Core Concept: Composes objects into tree structures to represent part-whole
 * hierarchies, allowing clients to treat individual objects (Leaves) and
 * compositions of objects (Composites) uniformly.
 */

// =========================================================================
// 1. COMPONENT INTERFACE (Common contract for Leaf and Composite)
// =========================================================================

interface CartItem {
    double getPrice();
    void display(String indent);
}

// =========================================================================
// 2. LEAF COMPONENT (Individual Atomic Product)
// =========================================================================

class Product implements CartItem {
    private final String name;
    private final double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📦 Product: " + name + " -> ₹" + price);
    }
}

// =========================================================================
// 3. COMPOSITE COMPONENT (Container holding child CartItems)
// =========================================================================

class ProductBundle implements CartItem {
    private final String bundleName;
    private final List<CartItem> items = new ArrayList<>();

    public ProductBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    @Override
    public double getPrice() {
        double total = 0;
        // Recursively aggregates prices of all children (Leaves & Nested Composites)
        for (CartItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 Bundle: [" + bundleName + "] (Total: ₹" + getPrice() + ")");
        for (CartItem item : items) {
            item.display(indent + "   ");
        }
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class CompositePatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🛒 E-Commerce Cart with Composite Pattern ===");

        // 1. Standalone Leaf Products
        CartItem book = new Product("Atomic Habits Book", 499.0);
        CartItem keyboard = new Product("Mechanical Keyboard", 4500.0);

        // 2. Composite: iPhone Essentials Combo (Contains Leaves)
        ProductBundle iphoneCombo = new ProductBundle("iPhone 15 Essentials Pack");
        iphoneCombo.addItem(new Product("iPhone 15 Pro", 129999.0));
        iphoneCombo.addItem(new Product("20W USB-C Fast Charger", 1999.0));
        iphoneCombo.addItem(new Product("MagSafe Clear Case", 4900.0));

        // 3. Composite: Back-to-School Stationery Kit (Contains Leaves)
        ProductBundle stationeryKit = new ProductBundle("Stationery Starter Kit");
        stationeryKit.addItem(new Product("A5 Spiral Notebooks (Pack of 3)", 249.0));
        stationeryKit.addItem(new Product("Gel Pen Set (10 Pens)", 99.0));

        // 4. Nested Composite: Student Mega Bundle (Contains Leaves + Another Composite Bundle!)
        ProductBundle studentMegaBundle = new ProductBundle("Student Mega Academic Combo");
        studentMegaBundle.addItem(new Product("Student Laptop Bag", 1899.0));
        studentMegaBundle.addItem(stationeryKit); // Nested composite bundle!

        // 5. Uniform Client Cart processing all items identically
        List<CartItem> cart = new ArrayList<>();
        cart.add(book);
        cart.add(keyboard);
        cart.add(iphoneCombo);
        cart.add(studentMegaBundle);

        // Render Cart Details
        System.out.println("\n--- Order Summary ---");
        double cartTotal = 0;
        for (CartItem item : cart) {
            item.display("  ");
            cartTotal += item.getPrice();
        }

        System.out.println("\n=================================");
        System.out.println("💳 Grand Total Checkout: ₹" + cartTotal);
        System.out.println("=================================");
    }
}
