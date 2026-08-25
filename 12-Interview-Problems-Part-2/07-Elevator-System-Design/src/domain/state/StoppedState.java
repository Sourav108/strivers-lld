package domain.state;

import domain.Elevator;

public class StoppedState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("🚪 [Doors Opening] Elevator #" + elevator.getId() + " at Floor " + elevator.getCurrentFloor());
        elevator.setState(new DoorsOpeningState());
    }

    @Override
    public void closeDoors(Elevator elevator) {
        System.out.println("Doors are already closed for Elevator #" + elevator.getId());
    }

    @Override
    public void startMoving(Elevator elevator) {
        System.out.println("🚀 [Moving] Elevator #" + elevator.getId() + " is now MOVING " + elevator.getDirection());
        elevator.setState(new MovingState());
    }

    @Override
    public void stop(Elevator elevator) {
        // Already stopped
    }

    @Override
    public void enterMaintenance(Elevator elevator) {
        System.out.println("🔧 [Maintenance] Elevator #" + elevator.getId() + " entering MAINTENANCE mode.");
        elevator.setActive(false);
        elevator.setState(new MaintenanceState());
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
        return "STOPPED";
    }
}
