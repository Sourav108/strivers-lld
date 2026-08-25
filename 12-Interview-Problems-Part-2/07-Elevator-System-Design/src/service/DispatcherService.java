package service;

import domain.Elevator;
import domain.ExternalRequest;
import domain.RequestStatus;
import domain.strategy.ElevatorSelectionStrategy;
import domain.strategy.NearestElevatorStrategy;
import repository.ElevatorRepository;
import repository.ExternalRequestRepository;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DispatcherService {
    private final ExternalRequestRepository externalRequestRepository;
    private final ElevatorRepository elevatorRepository;
    private ElevatorSelectionStrategy elevatorSelectionStrategy;
    private final BlockingQueue<ExternalRequest> requestQueue = new LinkedBlockingQueue<>();

    public DispatcherService(ExternalRequestRepository externalRequestRepository,
                             ElevatorRepository elevatorRepository) {
        this.externalRequestRepository = externalRequestRepository;
        this.elevatorRepository = elevatorRepository;
        this.elevatorSelectionStrategy = new NearestElevatorStrategy(); // default strategy
    }

    public void setElevatorSelectionStrategy(ElevatorSelectionStrategy strategy) {
        this.elevatorSelectionStrategy = strategy;
        System.out.println("🔄 [Selection Strategy Updated] Dispatcher now using: " + strategy.getClass().getSimpleName());
    }

    public void queueExternalRequest(ExternalRequest request) {
        request.setStatus(RequestStatus.QUEUED);
        externalRequestRepository.save(request);
        requestQueue.offer(request);
    }

    public Elevator selectBestElevator(ExternalRequest request, List<Elevator> availableElevators) {
        return elevatorSelectionStrategy.selectElevator(request, availableElevators);
    }

    public void assignRequestToElevator(ExternalRequest request, Elevator elevator) {
        request.setStatus(RequestStatus.ASSIGNED);
        request.setAssignedElevatorId(elevator.getId());
        externalRequestRepository.save(request);

        elevator.addStop(request.getFloorNumber());
        elevatorRepository.save(elevator);

        System.out.println("🎛️ [Dispatch Decision] Assigned Floor " + request.getFloorNumber() +
                " (" + request.getDirection() + ") to Elevator #" + elevator.getId() +
                " (Current: Floor " + elevator.getCurrentFloor() + ")");
    }

    public synchronized void processPendingRequests(String buildingId) {
        List<Elevator> available = elevatorRepository.findAvailableElevators(buildingId);
        if (available.isEmpty()) return;

        ExternalRequest req;
        while ((req = requestQueue.poll()) != null) {
            Elevator best = selectBestElevator(req, available);
            if (best != null) {
                assignRequestToElevator(req, best);
            } else {
                // Re-queue if no elevator currently available
                req.setStatus(RequestStatus.QUEUED);
                externalRequestRepository.save(req);
            }
        }
    }
}
