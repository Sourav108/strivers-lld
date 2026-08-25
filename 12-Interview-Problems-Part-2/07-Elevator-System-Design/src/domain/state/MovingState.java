package domain.state;

import domain.Elevator;

public class MovingState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("⚠️ [SAFETY HAZARD] Cannot open doors while Elevator #" + elevator.getId() + " is MOVING!");
    }

    @Override
    public void closeDoors(Elevator elevator) {
        // Doors already closed
    }

    @Override
    public void startMoving(Elevator elevator) {
        // Already moving
    }

    @Override
    public void stop(Elevator elevator) {
        System.out.println("🛑 [Stopped] Elevator #" + elevator.getId() + " reached Floor " + elevator.getCurrentFloor() + " and STOPPED.");
        elevator.setState(new StoppedState());
    }

    @Override
    public void enterMaintenance(Elevator elevator) {
        System.out.println("⏳ [Pre-Maintenance] Elevator #" + elevator.getId() + " will enter maintenance after completing current movement.");
        elevator.setState(new PreMaintenanceState());
    }

    @Override
    public void exitMaintenance(Elevator elevator) {
        // Not in maintenance
    }

    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        return elevator.isActive() && !elevator.isFull();
    }

    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        return elevator.isActive();
    }

    @Override
    public String getStateName() {
        return "MOVING";
    }
}
