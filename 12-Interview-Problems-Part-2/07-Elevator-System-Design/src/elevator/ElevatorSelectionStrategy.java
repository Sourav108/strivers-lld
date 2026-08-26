package elevator;

import java.util.List;

/**
 * Strategy interface for selecting the most suitable elevator for an external request.
 */
public interface ElevatorSelectionStrategy {
    /**
     * Selects the optimal elevator from the available fleet.
     *
     * @param elevators list of all elevators in the system
     * @param request   the incoming external hall request
     * @return the selected Elevator, or null if no elevator can serve the request
     */
    Elevator selectElevator(List<Elevator> elevators, Request request);
}
