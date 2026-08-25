package repository;

import domain.ExternalRequest;
import domain.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ExternalRequestRepository {
    ExternalRequest save(ExternalRequest request);
    Optional<ExternalRequest> findById(String requestId);
    List<ExternalRequest> findPendingRequests(String buildingId);
    List<ExternalRequest> findQueuedRequests(String buildingId);
    List<ExternalRequest> findAll();
    void updateRequestStatus(String requestId, RequestStatus status);
    void deleteById(String requestId);
}
