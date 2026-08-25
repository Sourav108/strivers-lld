package domain.strategy;

import domain.Elevator;
import domain.ExternalRequest;

import java.util.List;

public class LoadBalancingStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(ExternalRequest request, List<Elevator> availableElevators) {
        if (availableElevators == null || availableElevators.isEmpty()) {
            return null;
        }

        Elevator bestElevator = null;
        int minLoad = Integer.MAX_VALUE;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : availableElevators) {
            if (!elevator.getState().canAcceptExternalRequests(elevator)) {
                continue;
            }

            int load = elevator.getCurrentLoad();
            int distance = Math.abs(elevator.getCurrentFloor() - request.getFloorNumber());

            if (load < minLoad || (load == minLoad && distance < minDistance)) {
                minLoad = load;
                minDistance = distance;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }
}
