package domain.strategy;

import domain.Direction;
import domain.Elevator;
import domain.InternalRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class ScanStrategy implements MovementStrategy {

    @Override
    public List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests) {
        TreeSet<Integer> allStops = elevator.getTargetStops();
        if (requests != null) {
            for (InternalRequest req : requests) {
                allStops.add(req.getDestinationFloor());
            }
        }

        if (allStops.isEmpty()) {
            return Collections.emptyList();
        }

        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();

        List<Integer> upPath = new ArrayList<>();
        List<Integer> downPath = new ArrayList<>();

        for (int stop : allStops) {
            if (stop >= currentFloor) {
                upPath.add(stop);
            } else {
                downPath.add(stop);
            }
        }

        // Sort ascending for UP, descending for DOWN
        Collections.sort(upPath);
        downPath.sort(Collections.reverseOrder());

        List<Integer> finalPath = new ArrayList<>();

        if (direction == Direction.UP || direction == Direction.IDLE) {
            finalPath.addAll(upPath);
            finalPath.addAll(downPath);
        } else {
            finalPath.addAll(downPath);
            finalPath.addAll(upPath);
        }

        return finalPath;
    }
}
