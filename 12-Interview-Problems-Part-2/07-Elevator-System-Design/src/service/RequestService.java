package service;

import domain.Direction;
import domain.ExternalRequest;
import domain.InternalRequest;
import domain.RequestStatus;
import repository.ExternalRequestRepository;
import repository.InternalRequestRepository;

import java.util.UUID;

public class RequestService {
    private final ExternalRequestRepository externalRequestRepository;
    private final InternalRequestRepository internalRequestRepository;

    public RequestService(ExternalRequestRepository externalRequestRepository,
                          InternalRequestRepository internalRequestRepository) {
        this.externalRequestRepository = externalRequestRepository;
        this.internalRequestRepository = internalRequestRepository;
    }

    public ExternalRequest createExternalRequest(int floor, Direction direction, String buildingId) {
        String id = "EXT-REQ-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ExternalRequest request = new ExternalRequest(id, floor, buildingId, direction);
        externalRequestRepository.save(request);
        System.out.println("🛎️ [Floor Button Pressed] Floor " + floor + " (" + direction + ") in Building #" + buildingId);
        return request;
    }

    public InternalRequest createInternalRequest(String elevatorId, int destinationFloor) {
        String id = "INT-REQ-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        InternalRequest request = new InternalRequest(id, elevatorId, destinationFloor);
        internalRequestRepository.save(request);
        System.out.println("🎯 [Elevator Panel Pressed] Destination Floor " + destinationFloor + " selected inside Elevator #" + elevatorId);
        return request;
    }

    public void completeRequest(String requestId) {
        externalRequestRepository.updateRequestStatus(requestId, RequestStatus.COMPLETED);
    }

    public void completeInternalRequest(String requestId) {
        internalRequestRepository.updateRequestStatus(requestId, RequestStatus.COMPLETED);
    }
}
