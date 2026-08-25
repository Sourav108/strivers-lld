package service;

import domain.Direction;
import domain.Elevator;
import domain.ExternalRequest;
import domain.InternalRequest;
import domain.RequestStatus;
import domain.strategy.MovementStrategy;
import domain.strategy.ScanStrategy;
import repository.ElevatorRepository;
import repository.ExternalRequestRepository;
import repository.InternalRequestRepository;

import java.util.List;

public class MovementService {
    private final ElevatorRepository elevatorRepository;
    private final InternalRequestRepository internalRequestRepository;
    private final ExternalRequestRepository externalRequestRepository;
    private MovementStrategy movementStrategy;

    public MovementService(ElevatorRepository elevatorRepository,
                           InternalRequestRepository internalRequestRepository,
                           ExternalRequestRepository externalRequestRepository) {
        this.elevatorRepository = elevatorRepository;
        this.internalRequestRepository = internalRequestRepository;
        this.externalRequestRepository = externalRequestRepository;
        this.movementStrategy = new ScanStrategy(); // default strategy
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
        System.out.println("🔄 [Movement Strategy Updated] Now using: " + strategy.getClass().getSimpleName());
    }

    public synchronized void processElevatorMovement(Elevator elevator) {
        if (!elevator.isActive()) {
            return;
        }

        List<InternalRequest> internalRequests = internalRequestRepository.findPendingByElevator(elevator.getId());
        List<Integer> path = movementStrategy.calculatePath(elevator, internalRequests);

        if (path.isEmpty()) {
            elevator.setDirection(Direction.IDLE);
            elevator.stop();
            elevatorRepository.save(elevator);
            return;
        }

        int targetFloor = path.get(0);
        int currentFloor = elevator.getCurrentFloor();

        if (currentFloor == targetFloor) {
            // Arrived at target floor!
            handleArrival(elevator, targetFloor, internalRequests);
        } else {
            // Move 1 floor towards target
            if (targetFloor > currentFloor) {
                elevator.setDirection(Direction.UP);
                elevator.startMoving();
                elevator.setCurrentFloor(currentFloor + 1);
            } else {
                elevator.setDirection(Direction.DOWN);
                elevator.startMoving();
                elevator.setCurrentFloor(currentFloor - 1);
            }

            System.out.println("🛗 Elevator #" + elevator.getId() + " is at Floor " + elevator.getCurrentFloor() +
                    " (Moving " + elevator.getDirection() + " to Floor " + targetFloor + ")");

            if (elevator.getCurrentFloor() == targetFloor) {
                handleArrival(elevator, targetFloor, internalRequests);
            }
        }

        elevatorRepository.save(elevator);
    }

    private void handleArrival(Elevator elevator, int floor, List<InternalRequest> internalRequests) {
        elevator.stop();
        elevator.openDoors();

        // Complete matching internal requests
        for (InternalRequest req : internalRequests) {
            if (req.getDestinationFloor() == floor) {
                req.setStatus(RequestStatus.COMPLETED);
                internalRequestRepository.save(req);
                elevator.setCurrentLoad(elevator.getCurrentLoad() - 1);
                System.out.println("   👤 Passenger exited at Floor " + floor + ". Current Load: " + elevator.getCurrentLoad());
            }
        }

        // Complete matching assigned external requests
        List<ExternalRequest> externalRequests = externalRequestRepository.findAll();
        for (ExternalRequest req : externalRequests) {
            if (req.getFloorNumber() == floor && elevator.getId().equals(req.getAssignedElevatorId()) && req.getStatus() == RequestStatus.ASSIGNED) {
                req.setStatus(RequestStatus.COMPLETED);
                externalRequestRepository.save(req);
                elevator.setCurrentLoad(elevator.getCurrentLoad() + 1);
                System.out.println("   👤 Passenger boarded at Floor " + floor + ". Current Load: " + elevator.getCurrentLoad());
            }
        }

        elevator.removeStop(floor);
        elevator.closeDoors();
    }

    public synchronized void processAllElevatorMovements(String buildingId) {
        List<Elevator> elevators = elevatorRepository.findByBuilding(buildingId);
        for (Elevator elevator : elevators) {
            processElevatorMovement(elevator);
        }
    }

    public boolean hasPendingRequests(String buildingId) {
        boolean hasExt = !externalRequestRepository.findPendingRequests(buildingId).isEmpty() ||
                         !externalRequestRepository.findQueuedRequests(buildingId).isEmpty();
        if (hasExt) return true;

        List<Elevator> elevators = elevatorRepository.findByBuilding(buildingId);
        for (Elevator e : elevators) {
            if (e.hasStops() || !internalRequestRepository.findPendingByElevator(e.getId()).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
