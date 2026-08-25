/**
 * Creational Design Patterns: Factory Pattern
 * 
 * Core Concept: Provides an interface for creating objects in a superclass,
 * but allows subclasses or specialized factory methods to alter the type of objects created.
 * 
 * Client code is decoupled from direct instantiation logic (the 'new' keyword).
 */

// =========================================================================
// ❌ BAD DESIGN (Violates Factory Pattern & OCP)
// Business logic directly instantiates concrete classes with if-else branching.
// =========================================================================

class BadLogisticsService {
    public void dispatch(String mode, String packageId) {
        // ❌ Direct instantiation mixed with business logic
        if ("Air".equalsIgnoreCase(mode)) {
            System.out.println("[BadAir] ✈️ Flying package #" + packageId + " via cargo plane.");
        } else if ("Road".equalsIgnoreCase(mode)) {
            System.out.println("[BadRoad] 🚚 Driving package #" + packageId + " via delivery truck.");
        } else {
            System.out.println("❌ Unsupported mode: " + mode);
        }
    }
}

// =========================================================================
// ✅ GOOD DESIGN (Adheres to Factory Pattern)
// =========================================================================

// Step 1: Product Interface
interface Logistics {
    void send(String packageId);
}

// Step 2: Concrete Products
class RoadLogistics implements Logistics {
    @Override
    public void send(String packageId) {
        System.out.println("🚚 [Road Transport] Delivering package #" + packageId + " via highway container truck.");
    }
}

class AirLogistics implements Logistics {
    @Override
    public void send(String packageId) {
        System.out.println("✈️ [Air Transport] Shipping package #" + packageId + " via express Boeing 777 cargo.");
    }
}

class SeaLogistics implements Logistics {
    @Override
    public void send(String packageId) {
        System.out.println("🚢 [Sea Transport] Shipping container for package #" + packageId + " via ocean freight vessel.");
    }
}

// Step 3: Factory Class encapsulating object creation logic
class LogisticsFactory {
    public static Logistics createLogistics(String mode) {
        if ("Road".equalsIgnoreCase(mode)) {
            return new RoadLogistics();
        } else if ("Air".equalsIgnoreCase(mode)) {
            return new AirLogistics();
        } else if ("Sea".equalsIgnoreCase(mode)) {
            return new SeaLogistics();
        }
        throw new IllegalArgumentException("Unsupported logistics mode: " + mode);
    }
}

// Step 4: Client Business Service decoupled from concrete transport classes
class LogisticsService {
    public void dispatchPackage(String mode, String packageId) {
        // Obtains the product polymorphically via the Factory
        Logistics logistics = LogisticsFactory.createLogistics(mode);
        logistics.send(packageId);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class FactoryPatternExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Direct Instantiation in Business Service ===");
        BadLogisticsService badService = new BadLogisticsService();
        badService.dispatch("Air", "PKG-1001");
        badService.dispatch("Road", "PKG-1002");

        System.out.println("\n=== ✅ 2. Good Design: Decoupled via Factory Pattern ===");
        LogisticsService service = new LogisticsService();

        // Dispatching through various transport modes via Factory
        service.dispatchPackage("Road", "PKG-2001");
        service.dispatchPackage("Air", "PKG-2002");
        service.dispatchPackage("Sea", "PKG-2003");
    }
}
