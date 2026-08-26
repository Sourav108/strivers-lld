package elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Facade orchestrating the building's elevator fleet, dispatching external and internal requests,
 * enforcing floor constraints, and advancing simulation steps.
 */
public class ElevatorSystem {
    private final int minFloor;
    private final int maxFloor;
    private final List<Elevator> elevators;
    private ElevatorSelectionStrategy selectionStrategy;

    public ElevatorSystem(int minFloor, int maxFloor, int numElevators, int elevatorCapacity) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.elevators = new ArrayList<>();
        int totalFloors = maxFloor - minFloor + 1;
        this.selectionStrategy = new NearestElevatorStrategy(totalFloors);

        for (int i = 1; i <= numElevators; i++) {
            elevators.add(new Elevator(i, minFloor, elevatorCapacity));
        }
    }

    /**
     * Sets or swaps the dispatching strategy at runtime (Strategy Pattern).
     */
    public void setSelectionStrategy(ElevatorSelectionStrategy selectionStrategy) {
        if (selectionStrategy != null) {
            this.selectionStrategy = selectionStrategy;
        }
    }

    /**
     * Handles external hall call from a floor panel.
     */
    public synchronized Elevator requestElevator(int floor, Direction direction) {
        if (!isValidFloor(floor)) {
            System.out.println("❌ Invalid floor request: " + floor + " (Allowed: " + minFloor + " to " + maxFloor + ")");
            return null;
        }

        Request request = new Request(floor, direction);
        Elevator selectedElevator = selectionStrategy.selectElevator(elevators, request);

        if (selectedElevator != null) {
            System.out.println("🎯 Dispatched " + request + " to Elevator " + selectedElevator.getId());
            selectedElevator.addStop(floor);
            return selectedElevator;
        } else {
            System.out.println("⚠️ No available elevator to serve " + request + " (Fleet may be in maintenance or full)");
            return null;
        }
    }

    /**
     * Handles internal car call from an elevator cabin panel.
     */
    public synchronized boolean selectDestination(int elevatorId, int destinationFloor) {
        if (!isValidFloor(destinationFloor)) {
            System.out.println("❌ Invalid destination floor: " + destinationFloor);
            return false;
        }

        Elevator elevator = getElevator(elevatorId);
        if (elevator == null) {
            System.out.println("❌ Elevator " + elevatorId + " not found!");
            return false;
        }

        System.out.println("🎯 Internal request: Destination Floor " + destinationFloor + " on Elevator " + elevatorId);
        return elevator.addStop(destinationFloor);
    }

    /**
     * Advances simulation by one step for all elevators.
     */
    public synchronized void step() {
        for (Elevator elevator : elevators) {
            elevator.moveStep();
        }
    }

    /**
     * Runs simulation steps continuously until all pending stops are completed and all cars are IDLE.
     */
    public synchronized void runUntilIdle(int maxSteps) {
        int steps = 0;
        while (hasActiveElevators() && steps < maxSteps) {
            steps++;
            System.out.println("\n--- Step " + steps + " ---");
            step();
        }
    }

    public synchronized boolean hasActiveElevators() {
        for (Elevator elevator : elevators) {
            if (elevator.hasPendingStops() || elevator.getState() != ElevatorState.IDLE) {
                if (!elevator.isUnderMaintenance()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets maintenance mode for a specific elevator.
     */
    public void setElevatorMaintenance(int elevatorId, boolean maintenance) {
        Elevator elevator = getElevator(elevatorId);
        if (elevator != null) {
            elevator.setMaintenance(maintenance);
        }
    }

    public Elevator getElevator(int id) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == id) {
                return elevator;
            }
        }
        return null;
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public boolean isValidFloor(int floor) {
        return floor >= minFloor && floor <= maxFloor;
    }

    public void displayStatus() {
        System.out.println("\n📊 Elevator Fleet Status:");
        for (Elevator elevator : elevators) {
            System.out.println("  " + elevator);
        }
    }
}
