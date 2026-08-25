/**
 * Behavioural Design Patterns: Strategy Pattern
 * 
 * Core Concept: Defines a family of interchangeable algorithms, encapsulates each
 * into a separate class, and allows switching the active algorithm at runtime.
 */

// =========================================================================
// 1. STRATEGY INTERFACE
// =========================================================================

interface MatchingStrategy {
    void match(String riderLocation);
}

// =========================================================================
// 2. CONCRETE STRATEGIES (Encapsulated Algorithms)
// =========================================================================

class NearestDriverStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("📍 [Nearest Driver Strategy] Scanning GPS coordinates for closest available vehicle near " + riderLocation);
    }
}

class SurgePriorityStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("⚡ [Surge Priority Strategy] High demand detected! Prioritizing surge-rate drivers and premium vehicles near " + riderLocation);
    }
}

class AirportQueueStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("🛫 [Airport Queue Strategy] Dispatching next driver in FIFO airport terminal queue for pickup at " + riderLocation);
    }
}

class VipDriverStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("🌟 [VIP Top-Rated Strategy] Assigning top 1% 5-star rated chauffeur for executive ride at " + riderLocation);
    }
}

// =========================================================================
// 3. CONTEXT CLASS (Maintains strategy reference and delegates execution)
// =========================================================================

class RideMatchingService {
    private MatchingStrategy strategy;

    // Constructor Injection
    public RideMatchingService(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    // Setter Injection (Enables dynamic runtime hot-swapping)
    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public void matchRider(String riderLocation) {
        strategy.match(riderLocation);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class StrategyPatternExample {
    public static void main(String[] args) {
        System.out.println("=== 🚖 Uber Ride Matching Service with Strategy Pattern ===");

        // 1. Regular Booking: Nearest Driver Strategy
        System.out.println("\n--- Case 1: Standard Suburban Ride ---");
        RideMatchingService rideService = new RideMatchingService(new NearestDriverStrategy());
        rideService.matchRider("Indiranagar 100ft Road");

        // 2. Peak Hours / Rain Surge: Switch Strategy Dynamically
        System.out.println("\n--- Case 2: Heavy Rain Peak Hours (Surge Mode) ---");
        rideService.setStrategy(new SurgePriorityStrategy());
        rideService.matchRider("MG Road Metro Station");

        // 3. Airport Pickup: Switch to FIFO Terminal Queue
        System.out.println("\n--- Case 3: Airport Arrival Terminal ---");
        rideService.setStrategy(new AirportQueueStrategy());
        rideService.matchRider("Kempegowda International Airport Terminal 2");

        // 4. Executive VIP Ride: Switch to VIP Strategy
        System.out.println("\n--- Case 4: Corporate Executive Ride ---");
        rideService.setStrategy(new VipDriverStrategy());
        rideService.matchRider("Four Seasons Hotel Lounge");
    }
}
