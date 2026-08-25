package domain;

import java.util.UUID;

public class EmergencyRequest {
    private final String id;
    private final int intersectionId;
    private final Direction emergencyDirection;
    private final int durationSeconds;
    private boolean isActive;

    public EmergencyRequest(int intersectionId, Direction emergencyDirection, int durationSeconds) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.intersectionId = intersectionId;
        this.emergencyDirection = emergencyDirection;
        this.durationSeconds = durationSeconds;
        this.isActive = true;
    }

    public String getId() { return id; }
    public int getIntersectionId() { return intersectionId; }
    public Direction getEmergencyDirection() { return emergencyDirection; }
    public int getDurationSeconds() { return durationSeconds; }
    public boolean isActive() { return isActive; }
    public void deactivate() { this.isActive = false; }

    @Override
    public String toString() {
        return "EmergencyRequest{" + "id='" + id + '\'' + ", direction=" + emergencyDirection + ", duration=" + durationSeconds + "s, active=" + isActive + '}';
    }
}
