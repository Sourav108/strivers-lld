package domain.state;

import domain.Elevator;

public interface ElevatorStateHandler {
    void openDoors(Elevator elevator);
    void closeDoors(Elevator elevator);
    void startMoving(Elevator elevator);
    void stop(Elevator elevator);
    void enterMaintenance(Elevator elevator);
    void exitMaintenance(Elevator elevator);
    boolean canAcceptExternalRequests(Elevator elevator);
    boolean canAcceptInternalRequests(Elevator elevator);
    String getStateName();
}
