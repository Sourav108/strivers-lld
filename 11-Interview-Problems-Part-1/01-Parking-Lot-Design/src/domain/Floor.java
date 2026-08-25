package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Floor {
    private final UUID id;
    private final int floorNumber;
    private final List<ParkingSlot> slots;

    public Floor(int floorNumber) {
        this.id = UUID.randomUUID();
        this.floorNumber = floorNumber;
        this.slots = new ArrayList<>();
    }

    public void addSlot(ParkingSlot slot) {
        slots.add(slot);
    }

    public List<ParkingSlot> getSlots() {
        return new ArrayList<>(slots);
    }

    public List<ParkingSlot> getAvailableSlots(Vehicle.VehicleType vehicleType) {
        List<ParkingSlot> availableSlots = new ArrayList<>();
        for (ParkingSlot slot : slots) {
            if (slot.getSlotType() == vehicleType && !slot.isOccupied()) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    public UUID getId() { return id; }
    public int getFloorNumber() { return floorNumber; }
    public int getTotalSlots() { return slots.size(); }
    public int getAvailableSlotsCount(Vehicle.VehicleType vehicleType) {
        return getAvailableSlots(vehicleType).size();
    }

    @Override
    public String toString() {
        return "Floor{" + "id=" + id + ", floorNumber=" + floorNumber + ", totalSlots=" + slots.size() + '}';
    }
}
