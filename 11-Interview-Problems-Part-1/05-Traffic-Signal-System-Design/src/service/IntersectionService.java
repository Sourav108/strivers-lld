package service;

import domain.Direction;
import domain.Intersection;
import domain.TrafficLight;
import repository.IntersectionRepository;

public class IntersectionService {
    private final IntersectionRepository intersectionRepository;

    public IntersectionService(IntersectionRepository intersectionRepository) {
        this.intersectionRepository = intersectionRepository;
    }

    public Intersection createIntersection(int id, String name) {
        Intersection intersection = new Intersection(id, name);
        intersectionRepository.save(intersection);
        return intersection;
    }

    public Intersection getIntersection(int id) {
        return intersectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intersection #" + id + " not found."));
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
