package elevator;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a physical elevator car with position tracking, LOOK/SCAN stop scheduling,
 * passenger load management, and safety state transitions.
 */
public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private final int capacity;
    private int currentLoad;

    // Ordered stops for LOOK/SCAN scheduling
    private final TreeSet<Integer> upStops;
    private final TreeSet<Integer> downStops;

    public Elevator(int id, int capacity) {
        this(id, 0, capacity);
    }

    public Elevator(int id, int initialFloor, int capacity) {
        this.id = id;
        this.currentFloor = initialFloor;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>();
    }

    /**
     * Adds a stop to the elevator's schedule.
     */
    public synchronized boolean addStop(int targetFloor) {
        if (state == ElevatorState.MAINTENANCE) {
            System.out.println("❌ Elevator " + id + " is in MAINTENANCE. Cannot accept stop at Floor " + targetFloor);
            return false;
        }

        if (targetFloor == currentFloor && state != ElevatorState.MOVING) {
            System.out.println("🚪 Elevator " + id + " is already at Floor " + targetFloor + ". Opening doors.");
            this.state = ElevatorState.DOORS_OPEN;
            return true;
        }

        if (targetFloor > currentFloor) {
            upStops.add(targetFloor);
            if (direction == Direction.IDLE) {
                direction = Direction.UP;
                state = ElevatorState.MOVING;
            }
        } else {
            downStops.add(targetFloor);
            if (direction == Direction.IDLE) {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING;
            }
        }

        System.out.println("📌 Elevator " + id + " scheduled stop at Floor " + targetFloor +
                " [UpStops: " + upStops + ", DownStops: " + downStops + "]");
        return true;
    }

    /**
     * Executes one simulation step (tick) for the elevator movement.
     */
    public synchronized void moveStep() {
        if (state == ElevatorState.MAINTENANCE) {
            return;
        }

        // If doors are open, cycle them to closed and determine next movement
        if (state == ElevatorState.DOORS_OPEN) {
            System.out.println("🚪 Elevator " + id + " doors CLOSING at Floor " + currentFloor);
            resolveNextDirection();
            return;
        }

        if (direction == Direction.UP) {
            currentFloor++;
            System.out.println("⬆️  Elevator " + id + " moving UP to Floor " + currentFloor);
            if (upStops.contains(currentFloor)) {
                upStops.remove(currentFloor);
                state = ElevatorState.DOORS_OPEN;
                System.out.println("🔔 Elevator " + id + " ARRIVED at Floor " + currentFloor + ". Doors OPENING.");
            }
        } else if (direction == Direction.DOWN) {
            currentFloor--;
            System.out.println("⬇️  Elevator " + id + " moving DOWN to Floor " + currentFloor);
            if (downStops.contains(currentFloor)) {
                downStops.remove(currentFloor);
                state = ElevatorState.DOORS_OPEN;
                System.out.println("🔔 Elevator " + id + " ARRIVED at Floor " + currentFloor + ". Doors OPENING.");
            }
        } else {
            resolveNextDirection();
        }
    }

    /**
     * Helper to resolve the next direction based on pending stops (LOOK/SCAN algorithm).
     */
    private void resolveNextDirection() {
        if (direction == Direction.UP) {
            if (!upStops.isEmpty()) {
                state = ElevatorState.MOVING;
            } else if (!downStops.isEmpty()) {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING;
                System.out.println("🔄 Elevator " + id + " reversing direction to DOWN.");
            } else {
                direction = Direction.IDLE;
                state = ElevatorState.IDLE;
                System.out.println("⏹️  Elevator " + id + " is now IDLE at Floor " + currentFloor);
            }
        } else if (direction == Direction.DOWN) {
            if (!downStops.isEmpty()) {
                state = ElevatorState.MOVING;
            } else if (!upStops.isEmpty()) {
                direction = Direction.UP;
                state = ElevatorState.MOVING;
                System.out.println("🔄 Elevator " + id + " reversing direction to UP.");
            } else {
                direction = Direction.IDLE;
                state = ElevatorState.IDLE;
                System.out.println("⏹️  Elevator " + id + " is now IDLE at Floor " + currentFloor);
            }
        } else { // IDLE
            if (!upStops.isEmpty()) {
                direction = Direction.UP;
                state = ElevatorState.MOVING;
            } else if (!downStops.isEmpty()) {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING;
            } else {
                state = ElevatorState.IDLE;
            }
        }
    }

    /**
     * Boards passengers into the elevator.
     */
    public synchronized boolean boardPassengers(int passengerCount) {
        if (passengerCount <= 0) return true;
        if (currentLoad + passengerCount > capacity) {
            System.out.println("⚠️ Elevator " + id + " capacity exceeded! (Capacity: " + capacity +
                    ", Current: " + currentLoad + ", Attempting to board: " + passengerCount + ")");
            return false;
        }
        currentLoad += passengerCount;
        System.out.println("👥 " + passengerCount + " passengers boarded Elevator " + id +
                " (Current Load: " + currentLoad + "/" + capacity + ")");
        return true;
    }

    /**
     * Unboards passengers from the elevator.
     */
    public synchronized void exitPassengers(int passengerCount) {
        if (passengerCount <= 0) return;
        currentLoad = Math.max(0, currentLoad - passengerCount);
        System.out.println("🚶 " + passengerCount + " passengers exited Elevator " + id +
                " (Current Load: " + currentLoad + "/" + capacity + ")");
    }

    /**
     * Sets the maintenance mode of the elevator.
     */
    public synchronized void setMaintenance(boolean maintenance) {
        if (maintenance) {
            this.state = ElevatorState.MAINTENANCE;
            this.direction = Direction.IDLE;
            this.upStops.clear();
            this.downStops.clear();
            System.out.println("🛠️ Elevator " + id + " entered MAINTENANCE mode. All stops cleared.");
        } else {
            this.state = ElevatorState.IDLE;
            this.direction = Direction.IDLE;
            System.out.println("✅ Elevator " + id + " exited MAINTENANCE mode and is now IDLE.");
        }
    }

    public synchronized boolean isFull() {
        return currentLoad >= capacity;
    }

    public synchronized boolean isUnderMaintenance() {
        return state == ElevatorState.MAINTENANCE;
    }

    public synchronized boolean hasPendingStops() {
        return !upStops.isEmpty() || !downStops.isEmpty() || state == ElevatorState.DOORS_OPEN;
    }

    public int getId() {
        return id;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized Direction getDirection() {
        return direction;
    }

    public synchronized ElevatorState getState() {
        return state;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getCurrentLoad() {
        return currentLoad;
    }

    public synchronized Set<Integer> getUpStops() {
        return Collections.unmodifiableSet(upStops);
    }

    public synchronized Set<Integer> getDownStops() {
        return Collections.unmodifiableSet(downStops);
    }

    @Override
    public synchronized String toString() {
        return "Elevator-" + id + "[Floor=" + currentFloor + ", Dir=" + direction +
                ", State=" + state + ", Load=" + currentLoad + "/" + capacity + "]";
    }
}
