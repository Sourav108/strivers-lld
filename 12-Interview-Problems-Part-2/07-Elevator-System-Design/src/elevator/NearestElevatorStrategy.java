package elevator;

import java.util.List;

/**
 * Strategy that selects the closest elevator moving towards the requested floor in the same direction,
 * or the closest idle elevator with available capacity.
 */
public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    private final int totalFloors;

    public NearestElevatorStrategy(int totalFloors) {
        this.totalFloors = Math.max(1, totalFloors);
    }

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator bestElevator = null;
        int minScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            // Exclude elevators under maintenance or full to capacity
            if (elevator.isUnderMaintenance() || elevator.isFull()) {
                continue;
            }

            int score = calculateScore(elevator, request);

            if (score < minScore) {
                minScore = score;
                bestElevator = elevator;
            } else if (score == minScore && bestElevator != null) {
                // Secondary tie-breaker: prefer the elevator with less passenger load
                if (elevator.getCurrentLoad() < bestElevator.getCurrentLoad()) {
                    bestElevator = elevator;
                }
            }
        }

        return bestElevator;
    }

    private int calculateScore(Elevator elevator, Request request) {
        int currentFloor = elevator.getCurrentFloor();
        int targetFloor = request.getFloor();
        int distance = Math.abs(currentFloor - targetFloor);

        // Case 1: Elevator is IDLE
        if (elevator.getDirection() == Direction.IDLE) {
            return distance;
        }

        // Case 2: Elevator is moving in the SAME direction as the request and is on the way
        if (elevator.getDirection() == request.getDirection()) {
            if (request.getDirection() == Direction.UP && currentFloor <= targetFloor) {
                return distance;
            }
            if (request.getDirection() == Direction.DOWN && currentFloor >= targetFloor) {
                return distance;
            }
        }

        // Case 3: Elevator is moving away or in opposite direction (add full round-trip penalty)
        return (2 * totalFloors) + distance;
    }
}
