public class TimingService {
    private final IntersectionService intersectionService;

    public TimingService(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
    }

    public void setSignalTiming(int intersectionId, Direction direction, int greenDurationSeconds) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        intersection.getSignalTiming(direction).setGreenDurationSeconds(greenDurationSeconds);
        System.out.println("⏱️ [Timing Config] Set " + direction + " green duration to " + greenDurationSeconds + "s.");
    }

    public int calculateOptimalGreenDuration(int vehicleCount) {
        if (vehicleCount < 5) return 10;
        if (vehicleCount <= 20) return 25;
        return 45; // Heavy traffic congestion
    }

    public void adjustTimingBasedOnTraffic(int intersectionId, Direction direction) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        SignalTiming timing = intersection.getSignalTiming(direction);
        if (timing.isDynamic()) {
            int vehicleCount = intersection.getVehicleCounter(direction).getCount();
            int optimalGreen = calculateOptimalGreenDuration(vehicleCount);
            timing.setGreenDurationSeconds(optimalGreen);
            System.out.println("⚡ [Dynamic Timing Adjustment] " + direction + " (" + vehicleCount + " cars) -> Adjusted Green Duration: " + optimalGreen + "s.");
        }
    }
}
