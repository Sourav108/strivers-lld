package repository;

import domain.Direction;
import domain.SignalTiming;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TimingRepository {
    private final Map<String, SignalTiming> timings = new ConcurrentHashMap<>();

    private String buildKey(int intersectionId, Direction direction) {
        return intersectionId + "_" + direction.name();
    }

    public void save(int intersectionId, SignalTiming timing) {
        timings.put(buildKey(intersectionId, timing.getDirection()), timing);
    }

    public Optional<SignalTiming> find(int intersectionId, Direction direction) {
        return Optional.ofNullable(timings.get(buildKey(intersectionId, direction)));
    }
}
