import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmergencyService {
    private final IntersectionService intersectionService;
    private final Map<Integer, EmergencyRequest> activeEmergencies = new ConcurrentHashMap<>();

    public EmergencyService(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
    }

    public synchronized EmergencyRequest requestEmergency(int intersectionId, Direction emergencyDirection, int durationSeconds) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        System.out.println("\n🚨 [EMERGENCY ALERT] Priority vehicle approaching from " + emergencyDirection + " at Intersection '" + intersection.getName() + "'!");

        // 1. Pause normal automatic cycle
        intersection.getCycle().pause();
        System.out.println("   ⏸️ Normal cycle paused at phase: " + intersection.getCycle().getCurrentDirection());

        // 2. Set emergency mode and safely turn all signals to RED
        intersection.setEmergencyMode(true, emergencyDirection);
        intersection.setAllSignalsToRed();
        System.out.println("   🔴 All signals safely transitioned to RED.");

        // 3. Turn emergency direction to GREEN
        TrafficLight emergencyLight = intersection.getTrafficLight(emergencyDirection);
        emergencyLight.turnGreen();
        System.out.println("   🟢 [" + emergencyDirection + "] Priority GREEN granted for " + durationSeconds + " seconds.");

        EmergencyRequest request = new EmergencyRequest(intersectionId, emergencyDirection, durationSeconds);
        activeEmergencies.put(intersectionId, request);
        return request;
    }

    public synchronized void endEmergency(int intersectionId) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        EmergencyRequest request = activeEmergencies.remove(intersectionId);
        if (request == null || !intersection.isEmergencyMode()) {
            System.out.println("⚠️ No active emergency found for intersection #" + intersectionId);
            return;
        }

        Direction emergencyDir = request.getEmergencyDirection();
        System.out.println("\n🏁 [EMERGENCY CLEARED] Priority vehicle passed " + emergencyDir + ". Restoring normal operations...");

        // 1. Transition emergency signal back to RED
        TrafficLight emergencyLight = intersection.getTrafficLight(emergencyDir);
        emergencyLight.emergencyTransitionToRed();
        System.out.println("   🔴 [" + emergencyDir + "] Safely transitioned back to RED.");

        // 2. Deactivate emergency mode
        request.deactivate();
        intersection.setEmergencyMode(false, null);

        // 3. Resume normal cycle from paused phase
        intersection.getCycle().resume();
        Direction resumedDirection = intersection.getCycle().getCurrentDirection();
        System.out.println("   ▶️ Normal cycle resumed from paused phase: " + resumedDirection);

        // Reactivate the resumed phase
        intersectionService.advancePhase(intersectionId, resumedDirection);
    }
}
