package domain.strategy;

import domain.Direction;
import domain.Elevator;
import domain.ExternalRequest;

import java.util.List;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(ExternalRequest request, List<Elevator> availableElevators) {
        if (availableElevators == null || availableElevators.isEmpty()) {
            return null;
        }

        Elevator bestElevator = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator elevator : availableElevators) {
            if (!elevator.getState().canAcceptExternalRequests(elevator)) {
                continue;
            }

            int distance = Math.abs(elevator.getCurrentFloor() - request.getFloorNumber());
            int cost = distance * 2; // base distance cost

            // Directional affinity bonus
            if (elevator.getDirection() == Direction.IDLE) {
                cost += 0; // Ideal candidate
            } else if (elevator.getDirection() == request.getDirection()) {
                // Moving towards the request in same direction
                if ((elevator.getDirection() == Direction.UP && elevator.getCurrentFloor() <= request.getFloorNumber()) ||
                    (elevator.getDirection() == Direction.DOWN && elevator.getCurrentFloor() >= request.getFloorNumber())) {
                    cost += 1;
                } else {
                    cost += 10; // moving same direction but already passed floor
                }
            } else {
                cost += 15; // moving opposite direction
            }

            // Tie-break with load
            cost += elevator.getCurrentLoad();

            if (cost < minCost) {
                minCost = cost;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }
}
