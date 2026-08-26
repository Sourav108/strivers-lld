package elevator;

/**
 * Internal button panel located inside an elevator cabin for floor selection.
 */
public class ElevatorPanel {
    private final int elevatorId;
    private final ElevatorSystem elevatorSystem;

    public ElevatorPanel(int elevatorId, ElevatorSystem elevatorSystem) {
        this.elevatorId = elevatorId;
        this.elevatorSystem = elevatorSystem;
    }

    public boolean pressFloorButton(int destinationFloor) {
        System.out.println("🔘 [Elevator " + elevatorId + " Cabin Panel] Destination Floor " +
                destinationFloor + " pressed.");
        return elevatorSystem.selectDestination(elevatorId, destinationFloor);
    }

    public void pressEmergencyStop() {
        System.out.println("🚨 [Elevator " + elevatorId + " Cabin Panel] EMERGENCY STOP pressed!");
        elevatorSystem.setElevatorMaintenance(elevatorId, true);
    }

    public int getElevatorId() {
        return elevatorId;
    }
}
