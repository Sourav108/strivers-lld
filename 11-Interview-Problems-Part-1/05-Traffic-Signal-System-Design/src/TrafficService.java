public class TrafficService {
    private final IntersectionService intersectionService;

    public TrafficService(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
    }

    public void updateVehicleCount(int intersectionId, Direction direction, int count) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        intersection.getVehicleCounter(direction).setCount(count);
        System.out.println("🚗 [Traffic Sensor] Updated " + direction + " vehicle density: " + count + " cars.");
    }

    public int getVehicleCount(int intersectionId, Direction direction) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        return intersection.getVehicleCounter(direction).getCount();
    }
}
