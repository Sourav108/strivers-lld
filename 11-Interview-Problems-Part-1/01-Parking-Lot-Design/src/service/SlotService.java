package service;

import domain.ParkingSlot;
import domain.Vehicle;
import repository.SlotRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SlotService {
    private final SlotRepository slotRepository;

    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public synchronized Optional<ParkingSlot> allocateSlot(Vehicle.VehicleType vehicleType) {
        return slotRepository.allocateSlot(vehicleType);
    }

    public synchronized void releaseSlot(UUID slotId) {
        slotRepository.releaseSlot(slotId);
    }

    public List<ParkingSlot> getAvailableSlots(Vehicle.VehicleType vehicleType) {
        return slotRepository.findAvailableSlots(vehicleType);
    }

    public Optional<ParkingSlot> getSlotById(UUID slotId) {
        return slotRepository.findById(slotId);
    }

    public List<ParkingSlot> getAllSlots() {
        return slotRepository.getAllSlots();
    }

    public void addSlot(ParkingSlot slot) {
        slotRepository.save(slot);
    }
}
