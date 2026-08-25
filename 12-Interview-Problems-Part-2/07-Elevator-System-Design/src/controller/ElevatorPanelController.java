package controller;

import domain.Elevator;
import domain.InternalRequest;
import service.ElevatorService;
import service.RequestService;

public class ElevatorPanelController {
    private final RequestService requestService;
    private final ElevatorService elevatorService;

    public ElevatorPanelController(RequestService requestService, ElevatorService elevatorService) {
        this.requestService = requestService;
        this.elevatorService = elevatorService;
    }

    public InternalRequest selectFloor(String elevatorId, int destinationFloor) {
        Elevator elevator = elevatorService.findById(elevatorId);
        if (!elevator.getState().canAcceptInternalRequests(elevator)) {
            throw new IllegalStateException("❌ Elevator #" + elevatorId + " cannot accept requests in state: " + elevator.getState().getStateName());
        }

        InternalRequest request = requestService.createInternalRequest(elevatorId, destinationFloor);
        elevator.addStop(destinationFloor);
        return request;
    }

    public void openDoors(String elevatorId) {
        elevatorService.findById(elevatorId).openDoors();
    }

    public void closeDoors(String elevatorId) {
        elevatorService.findById(elevatorId).closeDoors();
    }
}
