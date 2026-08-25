package service;

import domain.Elevator;
import repository.ElevatorRepository;

import java.util.List;

public class ElevatorService {
    private final ElevatorRepository elevatorRepository;

    public ElevatorService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public Elevator createElevator(String buildingId, int capacity) {
        List<Elevator> existing = elevatorRepository.findByBuilding(buildingId);
        String id = "ELEV-" + (existing.size() + 1);
        Elevator elevator = new Elevator(id, buildingId, capacity);
        elevatorRepository.save(elevator);
        System.out.println("🛗 [Elevator Created] " + elevator);
        return elevator;
    }

    public void updateElevatorFloor(String elevatorId, int floor) {
        Elevator elevator = findById(elevatorId);
        elevator.setCurrentFloor(floor);
        elevatorRepository.save(elevator);
    }

    public List<Elevator> getAvailableElevators(String buildingId) {
        return elevatorRepository.findAvailableElevators(buildingId);
    }

    public List<Elevator> findByBuilding(String buildingId) {
        return elevatorRepository.findByBuilding(buildingId);
    }

    public Elevator findById(String elevatorId) {
        return elevatorRepository.findById(elevatorId)
                .orElseThrow(() -> new IllegalArgumentException("Elevator #" + elevatorId + " not found."));
    }

    public void setMaintenance(String elevatorId, boolean maintenance) {
        Elevator elevator = findById(elevatorId);
        if (maintenance) {
            elevator.enterMaintenance();
        } else {
            elevator.exitMaintenance();
        }
        elevatorRepository.save(elevator);
    }
}
