import controller.EmergencyController;
import controller.IntersectionController;
import controller.TimingController;
import controller.TrafficController;
import domain.Direction;
import domain.Intersection;
import domain.TrafficLight;
import domain.state.InvalidStateTransitionException;
import repository.EmergencyRepository;
import repository.IntersectionRepository;
import repository.TimingRepository;
import repository.TrafficRepository;
import service.EmergencyService;
import service.IntersectionService;
import service.TimingService;
import service.TrafficService;

/**
 * TrafficSignalSystem: Complete Simulation Driver for the Traffic Signal Control System
 * 
 * Demonstrates:
 * 1. Intersection setup with 4 traffic lights (NORTH, EAST, SOUTH, WEST)
 * 2. Normal automatic cycle phase transitions (RED -> GREEN -> YELLOW -> RED)
 * 3. State Pattern safety enforcement (InvalidStateTransitionException prevention)
 * 4. Emergency Vehicle Priority Override (Cycle Pause, All-Red Safety, Priority Green, and Cycle Resume)
 * 5. Vehicle Density Counting & Dynamic Green Light Timing Adjustments
 */

public class TrafficSignalSystem {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🚦 SMART TRAFFIC SIGNAL CONTROL SYSTEM - LLD ARCHITECTURE DEMO");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        IntersectionRepository intersectionRepo = new IntersectionRepository();
        EmergencyRepository emergencyRepo = new EmergencyRepository();
        TimingRepository timingRepo = new TimingRepository();
        TrafficRepository trafficRepo = new TrafficRepository();

        // --- 2. INITIALIZE SERVICES ---
        IntersectionService intersectionService = new IntersectionService(intersectionRepo);
        EmergencyService emergencyService = new EmergencyService(intersectionService, emergencyRepo);
        TimingService timingService = new TimingService(intersectionService, timingRepo);
        TrafficService trafficService = new TrafficService(intersectionService, trafficRepo);

        // --- 3. INITIALIZE CONTROLLERS ---
        IntersectionController intersectionController = new IntersectionController(intersectionService);
        EmergencyController emergencyController = new EmergencyController(emergencyService);
        TimingController timingController = new TimingController(timingService);
        TrafficController trafficController = new TrafficController(trafficService);

        // --- 4. CREATE INTERSECTION ---
        int intersectionId = 101;
        intersectionController.createIntersection(intersectionId, "Silk Board Junction");
        Intersection intersection = intersectionController.getIntersection(intersectionId);
        System.out.println("\n📍 Created: " + intersection);

        // =========================================================================
        // SCENARIO 1: NORMAL AUTOMATIC CYCLE PHASES
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Normal Automatic Cycle Transitions");
        System.out.println("-----------------------------------------------------------");

        System.out.println("▶️ Phase 1: Activate NORTH...");
        intersectionController.advancePhase(intersectionId, Direction.NORTH);
        intersectionController.displayStatus(intersectionId);

        System.out.println("\n▶️ Phase 2: Advance to EAST...");
        intersectionController.advancePhase(intersectionId, Direction.EAST);
        intersectionController.displayStatus(intersectionId);

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
        intersectionController.advancePhase(intersectionId, Direction.SOUTH);
        intersection.getCycle().getNextDirection(); // Advance cycle pointer to SOUTH
        intersectionController.displayStatus(intersectionId);

        // Emergency ambulance arrives from WEST!
        emergencyController.requestEmergency(intersectionId, Direction.WEST, 15);
        intersectionController.displayStatus(intersectionId);
        emergencyController.getEmergencyStatus(intersectionId);

        // Ambulance clears the intersection -> Restore normal cycle
        emergencyController.endEmergency(intersectionId);
        intersectionController.displayStatus(intersectionId);

        // =========================================================================
        // SCENARIO 4: VEHICLE COUNTING & DYNAMIC TIMING ADJUSTMENT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Traffic Density Detection & Dynamic Timing");
        System.out.println("-----------------------------------------------------------");

        // Heavy traffic arrives from NORTH (35 vehicles detected)
        trafficController.updateVehicleCount(intersectionId, Direction.NORTH, 35);
        trafficController.updateVehicleCount(intersectionId, Direction.WEST, 3);

        // Dynamic timing calculation adjusts green duration
        timingController.adjustTimingBasedOnTraffic(intersectionId, Direction.NORTH);
        timingController.adjustTimingBasedOnTraffic(intersectionId, Direction.WEST);

        System.out.println("\nUpdated Signal Timings:");
        for (Direction d : Direction.values()) {
            System.out.println("   " + timingController.getSignalTiming(intersectionId, d));
        }

        System.out.println("\n=================================================================");
        System.out.println("🎯 TRAFFIC SIGNAL SYSTEM ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
