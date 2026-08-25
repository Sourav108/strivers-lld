import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Strategic Guide on How to Approach a Low-Level Design (LLD) Interview
 * Case Study: Complete Smart Parking Lot System (Designed using the 9-Step Framework)
 * 
 * Demonstrates:
 * 1. Step 1 & 2: Domain Entities (Vehicle, ParkingSlot, Ticket)
 * 2. Step 4: Layered Architecture & SOLID Principles (Interface Segregation & DIP)
 * 3. Step 6: Design Patterns (Strategy Pattern for Allocation & Pricing)
 * 4. Step 7: Concurrency & Edge Case Handling (ReentrantLock to prevent Double Booking)
 * 5. Step 8 & 9: Clean Code, Extensibility, and Unit-Ready Architecture
 */

public class LLDInterviewFrameworkExample {

    // =========================================================================
    // STEP 2: DEFINE CORE ENUMS & DOMAIN ENTITIES
    // =========================================================================

    enum VehicleType { BIKE, CAR, TRUCK }
    enum SlotStatus { FREE, OCCUPIED }

    // Polymorphic Vehicle Hierarchy (Liskov Substitution Principle)
    abstract static class Vehicle {
        private final String licensePlate;
        private final VehicleType type;

        public Vehicle(String licensePlate, VehicleType type) {
            this.licensePlate = licensePlate;
            this.type = type;
        }

        public String getLicensePlate() { return licensePlate; }
        public VehicleType getType() { return type; }
    }

    static class Car extends Vehicle {
        public Car(String licensePlate) { super(licensePlate, VehicleType.CAR); }
    }

    static class Bike extends Vehicle {
        public Bike(String licensePlate) { super(licensePlate, VehicleType.BIKE); }
    }

    // Parking Slot Entity
    static class ParkingSlot {
        private final int slotNumber;
        private final VehicleType supportedType;
        private SlotStatus status;
        private Vehicle parkedVehicle;

        public ParkingSlot(int slotNumber, VehicleType supportedType) {
            this.slotNumber = slotNumber;
            this.supportedType = supportedType;
            this.status = SlotStatus.FREE;
        }

        public void assignVehicle(Vehicle vehicle) {
            this.parkedVehicle = vehicle;
            this.status = SlotStatus.OCCUPIED;
        }

        public void releaseVehicle() {
            this.parkedVehicle = null;
            this.status = SlotStatus.FREE;
        }

        public int getSlotNumber() { return slotNumber; }
        public VehicleType getSupportedType() { return supportedType; }
        public SlotStatus getStatus() { return status; }
        public Vehicle getParkedVehicle() { return parkedVehicle; }
    }

    // Ticket Entity
    static class Ticket {
        private final String ticketId;
        private final Vehicle vehicle;
        private final ParkingSlot slot;
        private final Instant entryTime;

        public Ticket(String ticketId, Vehicle vehicle, ParkingSlot slot, Instant entryTime) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.slot = slot;
            this.entryTime = entryTime;
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public ParkingSlot getSlot() { return slot; }
        public Instant getEntryTime() { return entryTime; }
    }

    // =========================================================================
    // STEP 6: APPLY DESIGN PATTERNS (Strategy Pattern for Allocation & Pricing)
    // =========================================================================

    // 1. Slot Allocation Strategy (Open/Closed Principle)
    interface ParkingStrategy {
        Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType);
    }

    // Concrete Strategy: Assign nearest slot to entrance (Lowest slot number)
    static class NearestSlotStrategy implements ParkingStrategy {
        @Override
        public Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType) {
            return slots.stream()
                    .filter(s -> s.getStatus() == SlotStatus.FREE && s.getSupportedType() == vehicleType)
                    .min(Comparator.comparingInt(ParkingSlot::getSlotNumber));
        }
    }

    // 2. Pricing Calculation Strategy (Open/Closed Principle)
    interface PricingStrategy {
        double calculateFee(long durationHours, VehicleType type);
    }

    // Concrete Strategy: Tiered Hourly Pricing
    static class HourlyPricingStrategy implements PricingStrategy {
        @Override
        public double calculateFee(long durationHours, VehicleType type) {
            long hours = Math.max(1, durationHours); // Minimum 1 hour charge
            switch (type) {
                case BIKE: return hours * 20.0;
                case CAR: return hours * 50.0;
                case TRUCK: return hours * 100.0;
                default: return hours * 50.0;
            }
        }
    }

    // =========================================================================
    // STEP 4 & 5: REPOSITORY & SERVICE LAYER (Dependency Inversion & Concurrency)
    // =========================================================================

    interface ParkingSlotRepository {
        List<ParkingSlot> findAll();
        void update(ParkingSlot slot);
    }

    static class InMemoryParkingSlotRepository implements ParkingSlotRepository {
        private final Map<Integer, ParkingSlot> slots = new ConcurrentHashMap<>();

        public void addSlot(ParkingSlot slot) { slots.put(slot.getSlotNumber(), slot); }

        @Override public List<ParkingSlot> findAll() { return new ArrayList<>(slots.values()); }
        @Override public void update(ParkingSlot slot) { slots.put(slot.getSlotNumber(), slot); }
    }

    static class ParkingLotService {
        private final ParkingSlotRepository slotRepository;
        private final ParkingStrategy parkingStrategy;
        private final PricingStrategy pricingStrategy;
        private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
        
        // Step 7: Concurrency Lock to eliminate Double-Booking race conditions
        private final ReentrantLock allocationLock = new ReentrantLock();

        public ParkingLotService(ParkingSlotRepository slotRepository, 
                                 ParkingStrategy parkingStrategy, 
                                 PricingStrategy pricingStrategy) {
            this.slotRepository = slotRepository;
            this.parkingStrategy = parkingStrategy;
            this.pricingStrategy = pricingStrategy;
        }

        // Hero Use Case 1: Park Vehicle
        public Ticket parkVehicle(Vehicle vehicle) {
            allocationLock.lock(); // Critical Section: Atomic search + occupy
            try {
                System.out.println("🚗 [" + vehicle.getLicensePlate() + " (" + vehicle.getType() + ")] Arrived at entrance...");
                
                List<ParkingSlot> allSlots = slotRepository.findAll();
                ParkingSlot assignedSlot = parkingStrategy.findSlot(allSlots, vehicle.getType())
                        .orElseThrow(() -> new IllegalStateException("❌ Parking Lot Full for Vehicle Type: " + vehicle.getType()));

                assignedSlot.assignVehicle(vehicle);
                slotRepository.update(assignedSlot);

                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                Ticket ticket = new Ticket(ticketId, vehicle, assignedSlot, Instant.now());
                activeTickets.put(ticketId, ticket);

                System.out.println("   🎟️ Assigned Slot #" + assignedSlot.getSlotNumber() + " -> Issued Ticket: " + ticketId);
                return ticket;
            } finally {
                allocationLock.unlock();
            }
        }

        // Hero Use Case 2: Unpark Vehicle & Settle Payment
        public double unparkVehicle(String ticketId, long simulatedParkedHours) {
            Ticket ticket = activeTickets.remove(ticketId);
            if (ticket == null) {
                throw new IllegalArgumentException("❌ Invalid or expired ticket ID: " + ticketId);
            }

            ParkingSlot slot = ticket.getSlot();
            Vehicle vehicle = ticket.getVehicle();

            allocationLock.lock();
            try {
                slot.releaseVehicle();
                slotRepository.update(slot);
            } finally {
                allocationLock.unlock();
            }

            double fee = pricingStrategy.calculateFee(simulatedParkedHours, vehicle.getType());
            System.out.println("🏁 [" + vehicle.getLicensePlate() + "] Vacated Slot #" + slot.getSlotNumber() + 
                               " after " + simulatedParkedHours + " hrs. Calculated Fee: ₹" + fee);
            return fee;
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM (Interview Demonstration Walkthrough)
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 🅿️ Smart Parking Lot System: 9-Step LLD Framework in Action ===");

        // Setup Repository & Seed Slots
        InMemoryParkingSlotRepository repository = new InMemoryParkingSlotRepository();
        repository.addSlot(new ParkingSlot(1, VehicleType.BIKE));
        repository.addSlot(new ParkingSlot(2, VehicleType.CAR));
        repository.addSlot(new ParkingSlot(3, VehicleType.CAR));

        // Inject Strategies & Initialize Service (DIP & OCP)
        ParkingStrategy nearestStrategy = new NearestSlotStrategy();
        PricingStrategy hourlyPricing = new HourlyPricingStrategy();
        ParkingLotService parkingService = new ParkingLotService(repository, nearestStrategy, hourlyPricing);

        // --- Demo 1: Park Vehicles ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ STEP 5 & 6 DEMO: Park Vehicles (Strategy & Slot Allocation)");
        System.out.println("-----------------------------------------------------------");
        Car car1 = new Car("KA-01-AB-1234");
        Bike bike1 = new Bike("DL-04-XY-9999");
        Car car2 = new Car("MH-12-CD-5678");

        Ticket t1 = parkingService.parkVehicle(car1);
        Ticket t2 = parkingService.parkVehicle(bike1);
        Ticket t3 = parkingService.parkVehicle(car2);

        // --- Demo 2: Edge Case Handling (Lot Full) ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ STEP 7 DEMO: Edge Case Handling (Parking Lot Capacity Exceeded)");
        System.out.println("-----------------------------------------------------------");
        Car car3 = new Car("WB-02-ZZ-0001");
        try {
            parkingService.parkVehicle(car3); // All car slots occupied!
        } catch (IllegalStateException e) {
            System.out.println("   Caught Expected Edge Case -> " + e.getMessage());
        }

        // --- Demo 3: Unpark and Calculate Fees ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ STEP 5 DEMO: Unpark & Settle Billing Strategy");
        System.out.println("-----------------------------------------------------------");
        parkingService.unparkVehicle(t1.getTicketId(), 3); // Car parked for 3 hours -> ₹150
        parkingService.unparkVehicle(t2.getTicketId(), 2); // Bike parked for 2 hours -> ₹40

        // --- Demo 4: Re-parking in Freed Slot ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ STEP 6 DEMO: Re-allocating Newly Freed Slot");
        System.out.println("-----------------------------------------------------------");
        Ticket t4 = parkingService.parkVehicle(car3); // Slot #2 is free again!

        System.out.println("\n===========================================================");
        System.out.println("🎯 Complete 9-Step LLD Interview Architecture Verified!");
        System.out.println("===========================================================");
    }
}
