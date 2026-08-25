/**
 * SOLID Principles: Interface Segregation Principle (ISP)
 * 
 * Core Concept: Clients should not be forced to depend on methods they do not use.
 * Break large, general-purpose "fat" interfaces into smaller, client-specific interfaces.
 */

// =========================================================================
// ❌ BAD DESIGN (Violates ISP: Fat Monolithic Interface)
// A single interface forces Rider and Driver to implement methods they don't need.
// =========================================================================

interface BadUberUser {
    void bookRide(String pickup, String destination);
    void rateDriver(int rating);
    void acceptRide(String rideId);
    void trackEarnings();
    void ratePassenger(int rating);
}

class BadRider implements BadUberUser {
    @Override
    public void bookRide(String pickup, String destination) {
        System.out.println("[BadRider] Booking ride from " + pickup + " to " + destination);
    }

    @Override
    public void rateDriver(int rating) {
        System.out.println("[BadRider] Rating driver: " + rating + "/5");
    }

    // ❌ FORCED UNNECESSARY METHODS:
    @Override
    public void acceptRide(String rideId) {
        throw new UnsupportedOperationException("Riders cannot accept rides!");
    }

    @Override
    public void trackEarnings() {
        throw new UnsupportedOperationException("Riders do not have earnings!");
    }

    @Override
    public void ratePassenger(int rating) {
        throw new UnsupportedOperationException("Riders cannot rate passengers!");
    }
}

class BadDriver implements BadUberUser {
    // ❌ FORCED UNNECESSARY METHODS:
    @Override
    public void bookRide(String pickup, String destination) {
        throw new UnsupportedOperationException("Drivers in driver-mode do not book rides!");
    }

    @Override
    public void rateDriver(int rating) {
        throw new UnsupportedOperationException("Drivers do not rate other drivers here!");
    }

    @Override
    public void acceptRide(String rideId) {
        System.out.println("[BadDriver] Accepted ride: " + rideId);
    }

    @Override
    public void trackEarnings() {
        System.out.println("[BadDriver] Tracking earnings: $120.00");
    }

    @Override
    public void ratePassenger(int rating) {
        System.out.println("[BadDriver] Rating passenger: " + rating + "/5");
    }
}

// =========================================================================
// ✅ GOOD DESIGN (Adheres to ISP: Segregated Role-Specific Interfaces)
// =========================================================================

// Interface 1: Dedicated solely to Rider capabilities
interface RiderOperations {
    void bookRide(String pickup, String destination);
    void rateDriver(int rating);
}

// Interface 2: Dedicated solely to Driver capabilities
interface DriverOperations {
    void acceptRide(String rideId);
    void trackEarnings();
    void ratePassenger(int rating);
}

// Interface 3: Easy extension for new roles (e.g. Uber Eats Delivery) without touching existing interfaces
interface DeliveryPartnerOperations {
    void acceptDelivery(String orderId);
    void completeDelivery(String orderId);
}

// Rider class only implements methods it actually cares about
class Rider implements RiderOperations {
    private final String name;

    public Rider(String name) {
        this.name = name;
    }

    @Override
    public void bookRide(String pickup, String destination) {
        System.out.println("🚖 [Rider: " + name + "] Booked ride from " + pickup + " to " + destination);
    }

    @Override
    public void rateDriver(int rating) {
        System.out.println("⭐ [Rider: " + name + "] Rated driver " + rating + "/5 stars.");
    }
}

// Driver class only implements driver-related operations
class Driver implements DriverOperations {
    private final String name;
    private double earnings;

    public Driver(String name, double initialEarnings) {
        this.name = name;
        this.earnings = initialEarnings;
    }

    @Override
    public void acceptRide(String rideId) {
        System.out.println("✅ [Driver: " + name + "] Accepted ride request #" + rideId);
    }

    @Override
    public void trackEarnings() {
        System.out.println("💰 [Driver: " + name + "] Total earnings: $" + earnings);
    }

    @Override
    public void ratePassenger(int rating) {
        System.out.println("⭐ [Driver: " + name + "] Rated passenger " + rating + "/5 stars.");
    }
}

// Seamless extension: Delivery partner implements only delivery operations
class DeliveryPartner implements DeliveryPartnerOperations {
    private final String name;

    public DeliveryPartner(String name) {
        this.name = name;
    }

    @Override
    public void acceptDelivery(String orderId) {
        System.out.println("🍔 [Delivery: " + name + "] Picked up food order #" + orderId);
    }

    @Override
    public void completeDelivery(String orderId) {
        System.out.println("📦 [Delivery: " + name + "] Delivered food order #" + orderId);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class ISPExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Bloated Interface Violations ===");
        BadUberUser badRider = new BadRider();
        badRider.bookRide("Koramangala", "Indiranagar");
        try {
            badRider.acceptRide("RIDE-999");
        } catch (UnsupportedOperationException e) {
            System.out.println("⚠️ Caught expected error: " + e.getMessage());
        }

        System.out.println("\n=== ✅ 2. Good Design: Segregated Interfaces ===");
        
        // Rider interaction through RiderOperations contract
        RiderOperations rider = new Rider("Sourav");
        rider.bookRide("HSR Layout", "MG Road");
        rider.rateDriver(5);

        System.out.println();

        // Driver interaction through DriverOperations contract
        DriverOperations driver = new Driver("Alex", 240.50);
        driver.acceptRide("RIDE-1024");
        driver.trackEarnings();
        driver.ratePassenger(5);

        System.out.println();

        // New role extension through DeliveryPartnerOperations
        DeliveryPartnerOperations deliveryPartner = new DeliveryPartner("John");
        deliveryPartner.acceptDelivery("ORDER-402");
        deliveryPartner.completeDelivery("ORDER-402");
    }
}
