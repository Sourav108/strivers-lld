package controller;

import domain.Direction;
import domain.ExternalRequest;
import service.BuildingService;
import service.DispatcherService;
import service.RequestService;

public class FloorPanelController {
    private final RequestService requestService;
    private final DispatcherService dispatcherService;
    private final BuildingService buildingService;

    public FloorPanelController(RequestService requestService,
                                DispatcherService dispatcherService,
                                BuildingService buildingService) {
        this.requestService = requestService;
        this.dispatcherService = dispatcherService;
        this.buildingService = buildingService;
    }

    public ExternalRequest pressUpButton(int floorNumber, String buildingId) {
        validateFloor(floorNumber, buildingId);
        ExternalRequest request = requestService.createExternalRequest(floorNumber, Direction.UP, buildingId);
        dispatcherService.queueExternalRequest(request);
        return request;
    }

    public ExternalRequest pressDownButton(int floorNumber, String buildingId) {
        validateFloor(floorNumber, buildingId);
        ExternalRequest request = requestService.createExternalRequest(floorNumber, Direction.DOWN, buildingId);
        dispatcherService.queueExternalRequest(request);
        return request;
    }

    private void validateFloor(int floorNumber, String buildingId) {
        if (!buildingService.isValidFloor(buildingId, floorNumber)) {
            throw new IllegalArgumentException("❌ Floor " + floorNumber + " is out of bounds for Building #" + buildingId);
        }
    }
}
