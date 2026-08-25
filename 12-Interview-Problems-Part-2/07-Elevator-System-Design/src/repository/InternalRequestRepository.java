package repository;

import domain.InternalRequest;
import domain.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface InternalRequestRepository {
    InternalRequest save(InternalRequest request);
    Optional<InternalRequest> findById(String requestId);
    List<InternalRequest> findByElevator(String elevatorId);
    List<InternalRequest> findPendingByElevator(String elevatorId);
    List<InternalRequest> findAll();
    void updateRequestStatus(String requestId, RequestStatus status);
}
