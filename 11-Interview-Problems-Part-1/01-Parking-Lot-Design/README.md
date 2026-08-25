# Parking Lot System - Low-Level Design

## Core Idea

A **Smart Multi-Floor Parking Lot System** automates slot allocation, ticket issuance, fee calculation, and payment settlement for multiple vehicle types (`BIKE`, `CAR`, `TRUCK`, `EV`) across multi-tier parking levels. Designed using a **Layered Architecture** (Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ Domain), it cleanly separates business logic from data storage, leverages the **Adapter Pattern** for swappable payment gateways, and ensures concurrency safety to prevent double-booking.

---

## Requirements / Problem

### Functional Requirements
- **Entry Flow:** Vehicle arrives at gate $\rightarrow$ System assigns available slot matching vehicle type $\rightarrow$ Generates active ticket $\rightarrow$ Marks slot as occupied.
- **Exit Flow:** Customer presents ticket $\rightarrow$ System calculates parking fee (hourly vs flat minimum) $\rightarrow$ Processes payment via payment gateway $\rightarrow$ Releases slot $\rightarrow$ Issues receipt $\rightarrow$ Deactivates ticket.
- **Admin Flow:** Add/manage floors, configure slot quotas per vehicle type, and update dynamic pricing rules.

### Non-Functional Requirements
- **Concurrency & Consistency:** Prevent race conditions where two simultaneous vehicles grab the same slot.
- **Extensibility:** Easily integrate new vehicle types (e.g. `EV`, `TRUCK`) or alternative payment gateways (e.g. `Razorpay`, `Stripe`) without modifying existing core services (Open/Closed Principle).
- **High Availability & Fault Tolerance:** Gracefully handle payment gateway outages with retries and fallback options.

---

## Main Classes

- **`Vehicle` (`domain/Vehicle.java`):** Domain model holding license plate and `VehicleType` (`BIKE`, `CAR`, `TRUCK`, `EV`).
- **`ParkingSlot` (`domain/ParkingSlot.java`):** Encapsulates slot identifier, supported vehicle type, floor number, and occupancy status.
- **`Floor` (`domain/Floor.java`):** Represents a parking floor containing a list of parking slots.
- **`Ticket` (`domain/Ticket.java`):** Represents active entry passes linking vehicles, slots, and entry timestamps.
- **`Receipt` (`domain/Receipt.java`):** Post-payment receipt capturing exit timestamp, final fee, and payment status.
- **`PricingRule` (`domain/PricingRule.java`):** Configurable fee matrix defining hourly and flat rates per vehicle type.
- **`PaymentGatewayAdapter` (`adapter/PaymentGatewayAdapter.java`):** Common interface abstracting external payment providers (`RazorpayAdapter`, `StripeAdapter`).
- **`SlotService` & `TicketService` (`service/`):** Core business logic orchestrators managing thread-safe slot allocations and ticket lifecycles.
- **`PricingService` (`service/PricingService.java`):** Computes fees using hybrid pricing rules (minimum of hourly vs flat rate).
- **`PaymentService` (`service/PaymentService.java`):** Handles payment processing with automated retries.
- **`EntryController`, `ExitController`, `AdminController` (`controller/`):** Boundary layer exposing API entrypoints and returning standard `EntryResult` / `ExitResult` DTOs.

---

## Class Relationships

```
+------------------+         +------------------+         +------------------+
| EntryController  | ------> |   SlotService    | ------> |  SlotRepository  |
| ExitController   | ------> |  TicketService   | ------> | TicketRepository |
| AdminController  | ------> |  PricingService  | ------> |PricingRuleRepo   |
+------------------+         |  PaymentService  | ------> |PaymentRepository |
                             +------------------+         +------------------+
                                      |                            |
                                      v                            v
                             +------------------+         +------------------+
                             | PaymentGateway   |         | Domain Entities  |
                             | Adapter (Razor/  |         | (Floor, Slot,    |
                             |  Stripe)         |         | Ticket, Receipt) |
                             +------------------+         +------------------+
```

---

## ❌ Bad Design (Monolithic Anti-Pattern)

```java
// ❌ Mixing everything in a monolithic God Class
class BadParkingLot {
    public void parkAndPay(String licensePlate, String type) {
        // Direct SQL query mixed in method
        // Hardcoded ₹50 fee calculation with if-else chains
        // Direct call to Razorpay API without interface abstraction
        // Zero concurrency locks: Two cars book the exact same slot!
    }
}
```

### What is wrong?
- ⚠️ **Tight Coupling:** Impossible to switch from Razorpay to Stripe without rewriting the core class.
- ⚠️ **Zero Extensibility (OCP Violation):** Adding EV charging or new pricing tiers forces code edits across multiple methods.
- ⚠️ **Race Conditions:** Under high concurrency, two cars arriving at separate gates can be assigned the exact same parking slot.

---

## ✅ Good Design (Layered Clean Architecture + Adapters)

```java
// 1. Adapter Pattern for Payment Extensibility (OCP / DIP)
public interface PaymentGatewayAdapter {
    boolean processPayment(double amount);
}

// 2. Thread-Safe Slot Allocation Service
public class SlotService {
    private final SlotRepository slotRepository;

    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public synchronized Optional<ParkingSlot> allocateSlot(Vehicle.VehicleType vehicleType) {
        return slotRepository.allocateSlot(vehicleType);
    }
}

// 3. Decoupled Entry Controller returning clean DTO
public class EntryController {
    public EntryResult enterVehicle(String licensePlate, Vehicle.VehicleType vehicleType) {
        Optional<ParkingSlot> slot = slotService.allocateSlot(vehicleType);
        if (slot.isEmpty()) return new EntryResult(false, null, null, "Parking Full");
        Ticket ticket = ticketService.generateTicket(new Vehicle(licensePlate, vehicleType), slot.get().getId());
        return new EntryResult(true, ticket.getId(), slot.get().getId(), "Parked Successfully");
    }
}
```

---

## How It Works

1. **Setup:** Admin initializes floors and assigns parking slots for `CAR`, `BIKE`, and `EV`, setting pricing rules via `AdminController`.
2. **Vehicle Entry:** Client calls `EntryController.enterVehicle("KA-01-1234", CAR)`. `SlotService` atomically reserves an available slot on Floor 1, and `TicketService` generates an active `Ticket`.
3. **Vehicle Exit:** Client calls `ExitController.exitVehicle(ticketId, CAR, 2, razorpay, RAZORPAY)`.
4. **Billing & Settlement:** `PricingService` calculates the fee. `PaymentService` attempts to charge the card via `RazorpayAdapter` with retry capabilities.
5. **Release:** Upon successful payment, `SlotService` marks the slot as free, `TicketService` deactivates the ticket, and `ReceiptService` issues a confirmed receipt.

---

## Design Decisions

1. **Layered Architecture:** Clear isolation between Controllers, Services, Repositories, Adapters, and Domain Models.
2. **Adapter Pattern for Payment Gateways:** Abstracting third-party integrations (`PaymentGatewayAdapter`) allows swapping `Razorpay` and `Stripe` at runtime.
3. **In-Memory Repositories with `ConcurrentHashMap`:** Eliminates external DB dependencies while maintaining thread-safe lookups.
4. **Hybrid Pricing Strategy:** Calculates both flat rate and hourly rate, applying the minimum to offer fair customer pricing.

---

## Patterns / SOLID Used

- **Single Responsibility Principle (SRP):** `PricingService` only calculates fees; `PaymentService` only executes transactions; `SlotService` only manages slot allocations.
- **Open/Closed Principle (OCP):** New payment gateways or vehicle types can be plugged in by creating new adapter implementations without altering existing services.
- **Dependency Inversion Principle (DIP):** High-level controllers and services depend on abstractions (`PaymentGatewayAdapter`, repositories) rather than concrete implementations.
- **Adapter Pattern:** Translates external payment provider interfaces into standard application calls.
- **Repository Pattern:** Encapsulates collection lookups and data persistence.

---

## Edge Cases Handled

1. **Parking Lot Full:** `EntryController` validates availability and returns a fail-safe `EntryResult(false, "No available slots")`.
2. **Payment Failure & Retries:** `PaymentService` retries failed transactions up to 2 times; if still failing, the barrier remains closed and allows switching to a fallback gateway.
3. **Re-allocation of Freed Slots:** When a vehicle exits, its slot immediately becomes available for incoming waiting vehicles.
4. **Invalid/Reused Tickets:** `ExitController` verifies `ticket.isActive()` to prevent duplicate exit processing.

---

## How to Run

Compile and run the self-contained simulation from the `01-Parking-Lot-Design` directory:

```bash
# Compile all Java source files
javac -d bin $(find src -name "*.java")

# Run the simulation driver
java -cp bin Main
```

---

## 🎯 Quick Summary

- **Core Idea:** A multi-floor parking lot system managing slot allocation, ticket issuance, and automated billing through a layered architecture.
- **Code Demonstrates:** End-to-end Entry, Exit, and Admin workflows with Payment Adapters, Thread-safe Repositories, and Pricing Rules.
- **LLD Takeaway:** Decouple domain entities from controllers using services and abstract external systems (payment/SMS) with the Adapter pattern.
- **Memorable Rule:** *"Allocate atomically on entry, calculate flexibly on exit, and abstract payment providers behind adapters."*
