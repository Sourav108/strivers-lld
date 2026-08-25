package repository;

import domain.Floor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FloorRepository {
    private final Map<UUID, Floor> floors = new ConcurrentHashMap<>();
    private final Map<Integer, UUID> floorNumberToId = new ConcurrentHashMap<>();

    public void save(Floor floor) {
        floors.put(floor.getId(), floor);
        floorNumberToId.put(floor.getFloorNumber(), floor.getId());
    }

    public Optional<Floor> findById(UUID id) {
        return Optional.ofNullable(floors.get(id));
    }

    public Optional<Floor> findByNumber(int floorNumber) {
        UUID id = floorNumberToId.get(floorNumber);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(floors.get(id));
    }

    public List<Floor> findAll() {
        return new ArrayList<>(floors.values());
    }

    public boolean existsByNumber(int floorNumber) {
        return floorNumberToId.containsKey(floorNumber);
    }
}
