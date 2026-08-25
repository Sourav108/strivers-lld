package service;

import domain.Direction;
import domain.Intersection;
import domain.VehicleCounter;
import repository.TrafficRepository;

public class TrafficService {
    private final IntersectionService intersectionService;
    private final TrafficRepository trafficRepository;

    public TrafficService(IntersectionService intersectionService, TrafficRepository trafficRepository) {
        this.intersectionService = intersectionService;
        this.trafficRepository = trafficRepository;
    }

    public void updateVehicleCount(int intersectionId, Direction direction, int count) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        VehicleCounter counter = intersection.getVehicleCounter(direction);
        counter.setCount(count);
        trafficRepository.save(intersectionId, counter);
        System.out.println("🚗 [Traffic Sensor] Updated " + direction + " vehicle density: " + count + " cars.");
    }

    public int getVehicleCount(int intersectionId, Direction direction) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        return intersection.getVehicleCounter(direction).getCount();
    }
}
