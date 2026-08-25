package domain.strategy;

import domain.Elevator;
import domain.ExternalRequest;

import java.util.List;

public interface ElevatorSelectionStrategy {
    Elevator selectElevator(ExternalRequest request, List<Elevator> availableElevators);
}
