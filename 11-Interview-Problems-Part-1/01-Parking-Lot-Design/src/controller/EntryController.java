package controller;

import domain.ParkingSlot;
import domain.Ticket;
import domain.Vehicle;
import service.SlotService;
import service.TicketService;

import java.util.Optional;
import java.util.UUID;

public class EntryController {
    public static class EntryResult {
        private final boolean success;
        private final UUID ticketId;
        private final UUID slotId;
        private final String message;

        public EntryResult(boolean success, UUID ticketId, UUID slotId, String message) {
            this.success = success;
            this.ticketId = ticketId;
            this.slotId = slotId;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public UUID getTicketId() { return ticketId; }
        public UUID getSlotId() { return slotId; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "EntryResult{" + "success=" + success + ", ticketId=" + ticketId + ", slotId=" + slotId + ", message='" + message + '\'' + '}';
        }
    }

    private final TicketService ticketService;
    private final SlotService slotService;

    public EntryController(TicketService ticketService, SlotService slotService) {
        this.ticketService = ticketService;
        this.slotService = slotService;
    }

    public EntryResult enterVehicle(String licensePlate, Vehicle.VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle(licensePlate, vehicleType);
        System.out.println("\n🚗 [Entry Gate] Vehicle arrived: " + vehicle);

        Optional<ParkingSlot> allocatedSlot = slotService.allocateSlot(vehicleType);
        if (allocatedSlot.isEmpty()) {
            System.out.println("   ❌ [Entry Denied] No available slots for vehicle type: " + vehicleType);
            return new EntryResult(false, null, null, "No available slots for type: " + vehicleType);
        }

        ParkingSlot slot = allocatedSlot.get();
        Ticket ticket = ticketService.generateTicket(vehicle, slot.getId());
        System.out.println("   🎟️ [Ticket Issued] Slot: Floor #" + slot.getFloorNumber() + " | Ticket ID: " + ticket.getId());

        return new EntryResult(true, ticket.getId(), slot.getId(), "Vehicle parked successfully");
    }
}
