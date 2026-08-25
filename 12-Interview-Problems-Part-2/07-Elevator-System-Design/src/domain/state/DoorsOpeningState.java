package domain.state;

import domain.Elevator;

public class DoorsOpeningState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("Doors are already OPEN for Elevator #" + elevator.getId());
    }

    @Override
    public void closeDoors(Elevator elevator) {
        System.out.println("🚪 [Doors Closing] Elevator #" + elevator.getId() + " doors are CLOSING.");
        elevator.setState(new DoorsClosingState());
    }

    @Override
    public void startMoving(Elevator elevator) {
        System.out.println("⚠️ [SAFETY HAZARD] Cannot move with doors OPEN! Closing doors first.");
        closeDoors(elevator);
        elevator.startMoving();
    }

    @Override
    public void stop(Elevator elevator) {
        // Already stationary
    }

    @Override
    public void enterMaintenance(Elevator elevator) {
        closeDoors(elevator);
        elevator.enterMaintenance();
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
        return "DOORS_OPENING";
    }
}
