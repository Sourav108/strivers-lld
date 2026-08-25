/**
 * Main Simulation Driver for the Traffic Signal System
 * Demonstrates:
 * 1. Intersection setup with 4 traffic lights (NORTH, EAST, SOUTH, WEST)
 * 2. Normal automatic cycle phase transitions (RED -> GREEN -> YELLOW -> RED)
 * 3. State Pattern safety enforcement (InvalidStateTransitionException prevention)
 * 4. Emergency Vehicle Priority Override (Cycle Pause, All-Red Safety, Priority Green, and Cycle Resume)
 * 5. Vehicle Density Counting & Dynamic Green Light Timing Adjustments
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🚦 SMART TRAFFIC SIGNAL CONTROL SYSTEM - LLD DEMONSTRATION");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE SERVICES ---
        IntersectionService intersectionService = new IntersectionService();
        EmergencyService emergencyService = new EmergencyService(intersectionService);
        TrafficService trafficService = new TrafficService(intersectionService);
        TimingService timingService = new TimingService(intersectionService);

        // --- 2. CREATE INTERSECTION ---
        int intersectionId = 101;
        Intersection intersection = intersectionService.createIntersection(intersectionId, "Silk Board Junction");
        System.out.println("\n📍 Created: " + intersection);

        // =========================================================================
        // SCENARIO 1: NORMAL AUTOMATIC CYCLE PHASES
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Normal Automatic Cycle Transitions");
        System.out.println("-----------------------------------------------------------");

        System.out.println("▶️ Phase 1: Activate NORTH...");
        intersectionService.advancePhase(intersectionId, Direction.NORTH);
        intersectionService.displayStatus(intersectionId);

        System.out.println("\n▶️ Phase 2: Advance to EAST...");
        intersectionService.advancePhase(intersectionId, Direction.EAST);
        intersectionService.displayStatus(intersectionId);

        // =========================================================================
        // SCENARIO 2: STATE PATTERN ENFORCEMENT & INVALID TRANSITIONS
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: State Pattern Safety (Invalid Transition Trap)");
        System.out.println("-----------------------------------------------------------");

        TrafficLight southLight = intersection.getTrafficLight(Direction.SOUTH);
        System.out.println("Current South Signal State: " + southLight.getStateName());

        try {
            System.out.println("⚠️ Attempting invalid direct transition: RED -> YELLOW...");
            southLight.turnYellow(); // Invalid! Must go RED -> GREEN
        } catch (InvalidStateTransitionException e) {
            System.out.println("   🛡️ Caught Expected State Violation -> " + e.getMessage());
        }

        // =========================================================================
        // SCENARIO 3: EMERGENCY VEHICLE PRIORITY OVERRIDE
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Emergency Vehicle Priority & Cycle Pause/Resume");
        System.out.println("-----------------------------------------------------------");

        // Advance to SOUTH phase before emergency
        intersectionService.advancePhase(intersectionId, Direction.SOUTH);
        intersection.getCycle().getNextDirection(); // Advance cycle pointer to SOUTH
        intersectionService.displayStatus(intersectionId);

        // Emergency ambulance arrives from WEST!
        emergencyService.requestEmergency(intersectionId, Direction.WEST, 15);
        intersectionService.displayStatus(intersectionId);

        // Ambulance clears the intersection -> Restore normal cycle
        emergencyService.endEmergency(intersectionId);
        intersectionService.displayStatus(intersectionId);

        // =========================================================================
        // SCENARIO 4: VEHICLE COUNTING & DYNAMIC TIMING ADJUSTMENT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Traffic Density Detection & Dynamic Timing");
        System.out.println("-----------------------------------------------------------");

        // Heavy traffic arrives from NORTH (35 vehicles detected)
        trafficService.updateVehicleCount(intersectionId, Direction.NORTH, 35);
        trafficService.updateVehicleCount(intersectionId, Direction.WEST, 3);

        // Dynamic timing calculation adjusts green duration
        timingService.adjustTimingBasedOnTraffic(intersectionId, Direction.NORTH);
        timingService.adjustTimingBasedOnTraffic(intersectionId, Direction.WEST);

        System.out.println("\nUpdated Signal Timings:");
        for (Direction d : Direction.values()) {
            System.out.println("   " + intersection.getSignalTiming(d));
        }

        System.out.println("\n=================================================================");
        System.out.println("🎯 TRAFFIC SIGNAL SYSTEM DEMONSTRATION COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
