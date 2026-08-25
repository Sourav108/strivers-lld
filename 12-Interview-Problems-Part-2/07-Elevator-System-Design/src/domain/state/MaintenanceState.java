package domain.state;

import domain.Elevator;

public class MaintenanceState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("🔧 [Maintenance Door Override] Opening doors for technician on Elevator #" + elevator.getId());
    }

    @Override
    public void closeDoors(Elevator elevator) {
        System.out.println("🔧 [Maintenance Door Override] Closing doors for technician on Elevator #" + elevator.getId());
    }

    @Override
    public void startMoving(Elevator elevator) {
        System.out.println("⚠️ Cannot service user movement while Elevator #" + elevator.getId() + " is in MAINTENANCE.");
    }

    @Override
    public void stop(Elevator elevator) {
        // Stationary
    }

    @Override
    public void enterMaintenance(Elevator elevator) {
        // Already in maintenance
    }

    @Override
    public void exitMaintenance(Elevator elevator) {
        System.out.println("🟢 [Maintenance Cleared] Elevator #" + elevator.getId() + " returning to service (STOPPED).");
        elevator.setActive(true);
        elevator.setState(new StoppedState());
    }

    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        return false; // Blocks all public external requests
    }

    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        return false; // Blocks public internal requests
    }

    @Override
    public String getStateName() {
        return "MAINTENANCE";
    }
}
