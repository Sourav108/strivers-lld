package domain.strategy;

import domain.Elevator;
import domain.InternalRequest;

import java.util.List;

public interface MovementStrategy {
    List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests);
}
