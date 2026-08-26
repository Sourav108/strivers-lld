package elevator;

/**
 * Encapsulates an elevator request (hall call or cabin call).
 */
public class Request {
    private final int floor;
    private final Direction direction;
    private final boolean isExternal;

    // External Request (Hall Call from a floor)
    public Request(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
        this.isExternal = true;
    }

    // Internal Request (Cabin Call inside an elevator)
    public Request(int destinationFloor) {
        this.floor = destinationFloor;
        this.direction = Direction.IDLE;
        this.isExternal = false;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isExternal() {
        return isExternal;
    }

    @Override
    public String toString() {
        if (isExternal) {
            return "ExternalRequest[Floor=" + floor + ", Direction=" + direction + "]";
        }
        return "InternalRequest[DestinationFloor=" + floor + "]";
    }
}
