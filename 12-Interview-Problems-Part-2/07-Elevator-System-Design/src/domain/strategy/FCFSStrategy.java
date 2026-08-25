package domain.strategy;

import domain.Elevator;
import domain.InternalRequest;

import java.util.ArrayList;
import java.util.List;

public class FCFSStrategy implements MovementStrategy {

    @Override
    public List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests) {
        List<Integer> path = new ArrayList<>();
        if (requests != null) {
            for (InternalRequest req : requests) {
                if (!path.contains(req.getDestinationFloor())) {
                    path.add(req.getDestinationFloor());
                }
            }
        }
        for (int stop : elevator.getTargetStops()) {
            if (!path.contains(stop)) {
                path.add(stop);
            }
        }
        return path;
    }
}
