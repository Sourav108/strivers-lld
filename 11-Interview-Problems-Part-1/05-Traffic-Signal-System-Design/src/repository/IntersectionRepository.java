package repository;

import domain.Intersection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class IntersectionRepository {
    private final Map<Integer, Intersection> intersections = new ConcurrentHashMap<>();

    public void save(Intersection intersection) {
        intersections.put(intersection.getId(), intersection);
    }

    public Optional<Intersection> findById(int id) {
        return Optional.ofNullable(intersections.get(id));
    }
}
