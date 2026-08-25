package domain;

import java.util.UUID;

public class ParkingSlot {
    private final UUID id;
    private final Vehicle.VehicleType slotType;
    private final int floorNumber;
    private boolean isOccupied;

    public ParkingSlot(Vehicle.VehicleType slotType, int floorNumber) {
        this.id = UUID.randomUUID();
        this.slotType = slotType;
        this.floorNumber = floorNumber;
        this.isOccupied = false;
    }

    public UUID getId() { return id; }
    public Vehicle.VehicleType getSlotType() { return slotType; }
    public int getFloorNumber() { return floorNumber; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }

    @Override
    public String toString() {
        return "ParkingSlot{" + "id=" + id + ", type=" + slotType + ", floor=" + floorNumber + ", occupied=" + isOccupied + '}';
    }
}
