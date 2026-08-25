package domain.state;

import domain.Elevator;

public class DoorsClosingState implements ElevatorStateHandler {

    @Override
    public void openDoors(Elevator elevator) {
        System.out.println("🚪 [Obstruction/Button] Reopening doors for Elevator #" + elevator.getId());
        elevator.setState(new DoorsOpeningState());
    }

    @Override
    public void closeDoors(Elevator elevator) {
        System.out.println("🚪 [Doors Closed] Elevator #" + elevator.getId() + " doors fully CLOSED.");
        elevator.setState(new StoppedState());
    }

    @Override
    public void startMoving(Elevator elevator) {
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
        return "DOORS_CLOSING";
    }
}
