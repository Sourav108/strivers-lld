package repository.impl;

import domain.Elevator;
import repository.ElevatorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ElevatorRepositoryImpl implements ElevatorRepository {
    private final Map<String, Elevator> elevators = new ConcurrentHashMap<>();

    @Override
    public Elevator save(Elevator elevator) {
        elevators.put(elevator.getId(), elevator);
        return elevator;
    }

    @Override
    public Optional<Elevator> findById(String elevatorId) {
        return Optional.ofNullable(elevators.get(elevatorId));
    }

    @Override
    public List<Elevator> findByBuilding(String buildingId) {
        return elevators.values().stream()
                .filter(e -> e.getBuildingId().equals(buildingId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Elevator> findAvailableElevators(String buildingId) {
        return elevators.values().stream()
                .filter(e -> e.getBuildingId().equals(buildingId) && e.isActive() && !e.isFull())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String elevatorId) {
        elevators.remove(elevatorId);
    }
}
