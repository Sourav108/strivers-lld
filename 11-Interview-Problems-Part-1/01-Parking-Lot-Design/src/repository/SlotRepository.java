package repository;

import domain.ParkingSlot;
import domain.Vehicle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SlotRepository {
    private final Map<UUID, ParkingSlot> slots = new ConcurrentHashMap<>();

    public ParkingSlot save(ParkingSlot slot) {
        slots.put(slot.getId(), slot);
        return slot;
    }

    public Optional<ParkingSlot> findById(UUID slotId) {
        return Optional.ofNullable(slots.get(slotId));
    }

    public List<ParkingSlot> findAvailableSlots(Vehicle.VehicleType vehicleType) {
        return slots.values().stream()
                .filter(s -> s.getSlotType() == vehicleType && !s.isOccupied())
                .collect(Collectors.toList());
    }

    public Optional<ParkingSlot> allocateSlot(Vehicle.VehicleType vehicleType) {
        for (ParkingSlot slot : slots.values()) {
            if (slot.getSlotType() == vehicleType && !slot.isOccupied()) {
                slot.setOccupied(true);
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    public void releaseSlot(UUID slotId) {
        ParkingSlot slot = slots.get(slotId);
        if (slot != null) {
            slot.setOccupied(false);
        }
    }

    public List<ParkingSlot> getAllSlots() {
        return new ArrayList<>(slots.values());
    }

    public Map<Vehicle.VehicleType, Long> getSlotStatistics() {
        return slots.values().stream()
                .filter(s -> !s.isOccupied())
                .collect(Collectors.groupingBy(ParkingSlot::getSlotType, Collectors.counting()));
    }
}
