package repository.impl;

import domain.InternalRequest;
import domain.RequestStatus;
import repository.InternalRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InternalRequestRepositoryImpl implements InternalRequestRepository {
    private final Map<String, InternalRequest> requests = new ConcurrentHashMap<>();

    @Override
    public InternalRequest save(InternalRequest request) {
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<InternalRequest> findById(String requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public List<InternalRequest> findByElevator(String elevatorId) {
        return requests.values().stream()
                .filter(r -> r.getElevatorId().equals(elevatorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<InternalRequest> findPendingByElevator(String elevatorId) {
        return requests.values().stream()
                .filter(r -> r.getElevatorId().equals(elevatorId) && r.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public List<InternalRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

    @Override
    public void updateRequestStatus(String requestId, RequestStatus status) {
        findById(requestId).ifPresent(r -> r.setStatus(status));
    }
}
