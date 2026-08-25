package controller;

import domain.Direction;
import service.TrafficService;

public class TrafficController {
    private final TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    public void updateVehicleCount(int intersectionId, Direction direction, int count) {
        trafficService.updateVehicleCount(intersectionId, direction, count);
    }

    public int getVehicleCount(int intersectionId, Direction direction) {
        return trafficService.getVehicleCount(intersectionId, direction);
    }
}
