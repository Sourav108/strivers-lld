package repository.impl;

import domain.Building;
import repository.BuildingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BuildingRepositoryImpl implements BuildingRepository {
    private final Map<String, Building> buildings = new ConcurrentHashMap<>();

    @Override
    public Building save(Building building) {
        buildings.put(building.getId(), building);
        return building;
    }

    @Override
    public Optional<Building> findById(String buildingId) {
        return Optional.ofNullable(buildings.get(buildingId));
    }

    @Override
    public List<Building> findAll() {
        return new ArrayList<>(buildings.values());
    }

    @Override
    public void deleteById(String buildingId) {
        buildings.remove(buildingId);
    }
}
