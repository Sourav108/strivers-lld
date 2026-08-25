package service;

import domain.Building;
import domain.SystemState;
import repository.BuildingRepository;

public class BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public Building createBuilding(String name, int minFloor, int maxFloor, int totalElevators) {
        String id = "BLDG-" + name.toUpperCase().replace(" ", "-").substring(0, Math.min(8, name.length()));
        Building building = new Building(id, name, minFloor, maxFloor, totalElevators);
        buildingRepository.save(building);
        System.out.println("🏢 [Building Initialized] " + building);
        return building;
    }

    public boolean isValidFloor(String buildingId, int floor) {
        return findById(buildingId).isValidFloor(floor);
    }

    public void setBuildingSystemState(String buildingId, SystemState state) {
        Building bldg = findById(buildingId);
        bldg.setSystemState(state);
        buildingRepository.save(bldg);
        System.out.println("🎛️ [System State Changed] Building #" + buildingId + " state is now " + state);
    }

    public boolean isSystemRunning(String buildingId) {
        return findById(buildingId).getSystemState() == SystemState.RUNNING;
    }

    public Building findById(String buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building #" + buildingId + " not found."));
    }

    public boolean buildingExists(String buildingId) {
        return buildingRepository.findById(buildingId).isPresent();
    }
}
