package repository;

import domain.EmergencyRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EmergencyRepository {
    private final Map<Integer, EmergencyRequest> activeEmergencies = new ConcurrentHashMap<>();

    public void save(EmergencyRequest request) {
        activeEmergencies.put(request.getIntersectionId(), request);
    }

    public Optional<EmergencyRequest> getActiveEmergency(int intersectionId) {
        return Optional.ofNullable(activeEmergencies.get(intersectionId));
    }

    public void remove(int intersectionId) {
        activeEmergencies.remove(intersectionId);
    }
}
