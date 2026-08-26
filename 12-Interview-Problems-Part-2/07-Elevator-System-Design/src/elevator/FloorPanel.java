package elevator;

/**
 * External button panel located at each floor to request an elevator.
 */
public class FloorPanel {
    private final int floorNumber;
    private final ElevatorSystem elevatorSystem;

    public FloorPanel(int floorNumber, ElevatorSystem elevatorSystem) {
        this.floorNumber = floorNumber;
        this.elevatorSystem = elevatorSystem;
    }

    public Elevator pressUpButton() {
        System.out.println("🔘 [Floor " + floorNumber + " Panel] UP button pressed.");
        return elevatorSystem.requestElevator(floorNumber, Direction.UP);
    }

    public Elevator pressDownButton() {
        System.out.println("🔘 [Floor " + floorNumber + " Panel] DOWN button pressed.");
        return elevatorSystem.requestElevator(floorNumber, Direction.DOWN);
    }

    public int getFloorNumber() {
        return floorNumber;
    }
}
