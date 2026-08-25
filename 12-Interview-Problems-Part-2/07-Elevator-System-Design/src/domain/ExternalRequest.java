package domain;

public class ExternalRequest {
    private final String id;
    private final int floorNumber;
    private final String buildingId;
    private final Direction direction;
    private final long timestamp;
    private RequestStatus status;
    private String assignedElevatorId;

    public ExternalRequest(String id, int floorNumber, String buildingId, Direction direction) {
        this.id = id;
        this.floorNumber = floorNumber;
        this.buildingId = buildingId;
        this.direction = direction;
        this.timestamp = System.currentTimeMillis();
        this.status = RequestStatus.PENDING;
    }

    public String getId() { return id; }
    public int getFloorNumber() { return floorNumber; }
    public String getBuildingId() { return buildingId; }
    public Direction getDirection() { return direction; }
    public long getTimestamp() { return timestamp; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getAssignedElevatorId() { return assignedElevatorId; }
    public void setAssignedElevatorId(String assignedElevatorId) { this.assignedElevatorId = assignedElevatorId; }

    @Override
    public String toString() {
        return "ExternalRequest[" + id + " | Floor: " + floorNumber + " " + direction + " | Status: " + status +
                (assignedElevatorId != null ? " | Elevator: " + assignedElevatorId : "") + "]";
    }
}
