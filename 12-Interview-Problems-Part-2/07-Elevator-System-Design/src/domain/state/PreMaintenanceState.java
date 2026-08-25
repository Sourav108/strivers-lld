package domain.state;

import domain.Elevator;

public class PreMaintenanceState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("⚠️ Cannot open doors while moving towards pre-maintenance stop on Elevator #" + elevator.getId());
    }

    @Override
    public void closeDoors(Elevator elevator) {
        // Closed
    }

    @Override
    public void startMoving(Elevator elevator) {
        // Moving to final stop
    }

    @Override
    public void stop(Elevator elevator) {
        System.out.println("🛑 [Pre-Maintenance Complete] Elevator #" + elevator.getId() + " reached floor and is now locked in MAINTENANCE.");
        elevator.setActive(false);
        elevator.setState(new MaintenanceState());
    }

    @Override
    public void enterMaintenance(Elevator elevator) {
        // Already entering maintenance
    }

    @Override
    public void exitMaintenance(Elevator elevator) {
        System.out.println("Transitioning Elevator #" + elevator.getId() + " back to STOPPED.");
        elevator.setActive(true);
        elevator.setState(new StoppedState());
    }

    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        return false; // Rejects new external assignments while draining
    }

    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        return false;
    }

    @Override
    public String getStateName() {
        return "PRE_MAINTENANCE";
    }
}
