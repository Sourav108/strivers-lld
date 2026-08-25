package domain;

public class InternalRequest {
    private final String id;
    private final String elevatorId;
    private final int destinationFloor;
    private final long timestamp;
    private RequestStatus status;

    public InternalRequest(String id, String elevatorId, int destinationFloor) {
        this.id = id;
        this.elevatorId = elevatorId;
        this.destinationFloor = destinationFloor;
        this.timestamp = System.currentTimeMillis();
        this.status = RequestStatus.PENDING;
    }

    public String getId() { return id; }
    public String getElevatorId() { return elevatorId; }
    public int getDestinationFloor() { return destinationFloor; }
    public long getTimestamp() { return timestamp; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "InternalRequest[" + id + " | Elevator: " + elevatorId + " -> DestFloor: " + destinationFloor + " | Status: " + status + "]";
    }
}
