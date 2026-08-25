import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IntersectionService {
    private final Map<Integer, Intersection> intersections = new ConcurrentHashMap<>();

    public Intersection createIntersection(int id, String name) {
        Intersection intersection = new Intersection(id, name);
        intersections.put(id, intersection);
        return intersection;
    }

    public Intersection getIntersection(int id) {
        Intersection intersection = intersections.get(id);
        if (intersection == null) {
            throw new IllegalArgumentException("Intersection #" + id + " not found.");
        }
        return intersection;
    }

    // Advance to a specific direction phase in a controlled, safe sequence
    public synchronized void advancePhase(int intersectionId, Direction greenDirection) {
        Intersection intersection = getIntersection(intersectionId);
        if (intersection.isEmergencyMode()) {
            System.out.println("⚠️ [Cycle Blocked] Intersection is in Emergency Mode. Normal cycle paused.");
            return;
        }

        // 1. Transition any currently GREEN signal to YELLOW then RED
        for (Direction d : Direction.values()) {
            TrafficLight light = intersection.getTrafficLight(d);
            if ("GREEN".equals(light.getStateName())) {
                light.turnYellow();
                System.out.println("   🟡 [" + d + "] Transitioned: GREEN -> YELLOW");
                light.turnRed();
                System.out.println("   🔴 [" + d + "] Transitioned: YELLOW -> RED");
            }
        }

        // 2. Turn the target direction to GREEN
        TrafficLight targetLight = intersection.getTrafficLight(greenDirection);
        targetLight.turnGreen();
        System.out.println("   🟢 [" + greenDirection + "] Transitioned: RED -> GREEN (Phase Active for " +
                intersection.getSignalTiming(greenDirection).getGreenDurationSeconds() + "s)");
    }

    public void displayStatus(int intersectionId) {
        System.out.println(getIntersection(intersectionId));
    }
}
