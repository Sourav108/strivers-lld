package repository;

import domain.Direction;
import domain.VehicleCounter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficRepository {
    private final Map<String, VehicleCounter> counters = new ConcurrentHashMap<>();

    private String buildKey(int intersectionId, Direction direction) {
        return intersectionId + "_" + direction.name();
    }

    public void save(int intersectionId, VehicleCounter counter) {
        counters.put(buildKey(intersectionId, counter.getDirection()), counter);
    }

    public VehicleCounter getCounter(int intersectionId, Direction direction) {
        return counters.computeIfAbsent(buildKey(intersectionId, direction), k -> new VehicleCounter(direction));
    }
}
