package controller;

import domain.Elevator;
import service.ElevatorSchedulerService;
import service.ElevatorService;

public class ElevatorController {
    private final ElevatorService elevatorService;
    private final ElevatorSchedulerService schedulerService;

    public ElevatorController(ElevatorService elevatorService, ElevatorSchedulerService schedulerService) {
        this.elevatorService = elevatorService;
        this.schedulerService = schedulerService;
    }

    public Elevator createElevator(String buildingId, int capacity) {
        return elevatorService.createElevator(buildingId, capacity);
    }

    public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
        elevatorService.setMaintenance(elevatorId, maintenance);
    }

    public void startElevatorSystem(String buildingId) {
        schedulerService.startScheduler(buildingId);
    }

    public void stopElevatorSystem(String buildingId) {
        schedulerService.stopScheduler(buildingId);
    }
}
