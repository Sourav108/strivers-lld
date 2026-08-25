package domain;

import domain.state.ElevatorStateHandler;
import domain.state.StoppedState;

import java.util.TreeSet;

public class Elevator {
    private final String id;
    private final String buildingId;
    private int currentFloor;
    private Direction direction;
    private final int capacity; // max passengers
    private int currentLoad;    // current passengers
    private boolean isActive;
    private ElevatorStateHandler stateHandler;
    private final TreeSet<Integer> targetStops = new TreeSet<>();

    public Elevator(String id, String buildingId, int capacity) {
        this.id = id;
        this.buildingId = buildingId;
        this.capacity = capacity;
        this.currentFloor = 0; // Ground floor by default
        this.direction = Direction.IDLE;
        this.currentLoad = 0;
        this.isActive = true;
        this.stateHandler = new StoppedState();
    }

    public String getId() { return id; }
    public String getBuildingId() { return buildingId; }
    public synchronized int getCurrentFloor() { return currentFloor; }
    public synchronized void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }

    public synchronized Direction getDirection() { return direction; }
    public synchronized void setDirection(Direction direction) { this.direction = direction; }

    public int getCapacity() { return capacity; }
    public synchronized int getCurrentLoad() { return currentLoad; }
    public synchronized void setCurrentLoad(int currentLoad) { this.currentLoad = Math.max(0, currentLoad); }

    public boolean isFull() { return currentLoad >= capacity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public ElevatorStateHandler getState() { return stateHandler; }
    public void setState(ElevatorStateHandler stateHandler) { this.stateHandler = stateHandler; }

    public synchronized void addStop(int floor) {
        targetStops.add(floor);
    }

    public synchronized void removeStop(int floor) {
        targetStops.remove(floor);
    }

    public synchronized boolean hasStops() {
        return !targetStops.isEmpty();
    }

    public synchronized TreeSet<Integer> getTargetStops() {
        return new TreeSet<>(targetStops);
    }

    // State action delegates
    public void openDoors() { stateHandler.openDoors(this); }
    public void closeDoors() { stateHandler.closeDoors(this); }
    public void startMoving() { stateHandler.startMoving(this); }
    public void stop() { stateHandler.stop(this); }
    public void enterMaintenance() { stateHandler.enterMaintenance(this); }
    public void exitMaintenance() { stateHandler.exitMaintenance(this); }

    @Override
    public String toString() {
        return "Elevator[" + id + " | Floor: " + currentFloor + " | " + direction +
                " | State: " + stateHandler.getStateName() + " | Load: " + currentLoad + "/" + capacity +
                " | Stops: " + targetStops + "]";
    }
}
