# 03 - How to Approach a Low-Level Design (LLD) Interview

## Core Idea

Succeeding in a 45–60 minute **Low-Level Design (LLD) Interview** requires a structured, collaborative, and phased engineering methodology rather than jumping straight into code. By executing a **9-Step Strategic Blueprint**, candidate engineers clarify ambiguous requirements, model domain entities, structure layered architectures with SOLID principles, apply appropriate design patterns, and proactively mitigate concurrency edge cases.

---

## 🧭 The 9-Step LLD Interview Blueprint

```
+---------------------------------------------------------------------------------------------------+
| THE 9-STEP LLD INTERVIEW FRAMEWORK                                                                |
|                                                                                                   |
| Step 1: Clarify Requirements          -> Functional, Non-Functional (NFRs), and Hero Use Cases.   |
| Step 2: Identify Core Entities        -> Extract Domain Objects, Attributes, and Relationships.   |
| Step 3: Visualize Interaction Flow    -> Sequence Diagrams & Controller -> Service -> Repo flows. |
| Step 4: Define Class Structures       -> Apply OOP, SOLID, Interfaces, and Abstract Classes.      |
| Step 5: Detail Core Methods & CRUD    -> Input/Output Models, Method Signatures, State Changes.   |
| Step 6: Apply Design Patterns         -> Strategy (Algorithms), Factory (Creation), Observer(Pub).|
| Step 7: Handle Edge Cases             -> Concurrency, Race Conditions, Idempotency, Timeouts.     |
| Step 8: Class Diagram & Package Org   -> Layered/Modular Package Layout and Clean Architecture.   |
| Step 9: Discuss Future Add-ons        -> Extensibility, Caching, Cloud Read-Replicas, Extensions. |
+---------------------------------------------------------------------------------------------------+
```

---

## 📋 Comprehensive Step-by-Step Breakdown

### Step 1: Clarify Requirements (5–8 Mins)
- **Functional Requirements (FR):** What the system must do (e.g. Park vehicle, generate ticket, calculate fee, process payment).
- **Non-Functional Requirements (NFR):** Concurrency safety, low latency ($<50\text{ms}$), high availability, modular extensibility.
- **Hero Use Cases:** The primary end-to-end happy path flow (e.g., Car arrives $\rightarrow$ Assigned slot $\rightarrow$ Ticket issued).

### Step 2: Identify Core Entities (5 Mins)
- **Primary Entities:** `Vehicle`, `ParkingSlot`, `Ticket`, `Payment`.
- **Auxiliary Enums / Value Objects:** `VehicleType`, `SlotStatus`, `PaymentMethod`.

### Step 3: Visualize Interaction Flow (5 Mins)
- Map request journeys: Client $\rightarrow$ Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ Database.

### Step 4: Define Class Structures & SOLID (10 Mins)
- **SRP:** Separate parking allocation logic from billing calculations.
- **OCP:** Add new slot allocation algorithms (e.g., nearest-to-entrance vs EV-priority) without modifying existing code.
- **DIP:** Inject repository and strategy interfaces into domain services.

### Step 5 & 6: Core Methods & Design Patterns (15 Mins)
- **Strategy Pattern:** Interchangeable slot selection (`NearestSlotStrategy`) and pricing algorithms (`HourlyPricingStrategy`).
- **Factory Pattern:** Dynamic instantiation of vehicle types (`VehicleFactory`).
- **Observer Pattern:** Broadcasting capacity alerts to LED display boards.

### Step 7: Handle Edge Cases & Concurrency (10 Mins)
- Prevent **Double Booking** under high concurrency with explicit locks (`ReentrantLock`).
- Ensure **Idempotency** on payment processing tickets.

### Step 8 & 9: Package Architecture & Future Extensions (5 Mins)
- Organize into `controller`, `service`, `strategy`, `repository`, and `model` packages.

---

## ❌ Bad Interview Behavior (The "Cowboy Coder" Anti-Pattern)

```java
// ❌ Jumping straight to writing a 300-line monolithic God Class without asking requirements!
class BadParkingLot {
    public void doEverything() {
        // ❌ Hardcoded pricing logic
        // ❌ No concurrency protection (Double booking bug!)
        // ❌ Direct console printing mixed with database queries
        // ❌ Violation of SRP, OCP, and DIP
    }
}
```

### What is wrong?
- ⚠️ **Zero Requirement Alignment:** Building features the interviewer never asked for while missing core hero requirements.
- ⚠️ **Monolithic Tight Coupling:** Mixing billing, database queries, and slot allocation in a single class.
- ⚠️ **Ignoring Concurrency:** Two concurrent cars grab the exact same slot.

---

## ✅ Good Design (Demonstrated on Smart Parking Lot)

```java
/**
 * 9-Step LLD Framework Implementation: Smart Parking Lot System
 */

// Step 6: Strategy Pattern for Dynamic Slot Assignment (OCP)
public interface ParkingStrategy {
    Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType);
}

// Step 4 & 6: Strategy Pattern for Dynamic Pricing
public interface PricingStrategy {
    double calculateFee(long durationHours, VehicleType vehicleType);
}

// Step 4 & 5: Service Layer with Injected Collaborators & Concurrency Safety
public class ParkingLotService {
    private final ParkingSlotRepository slotRepository;
    private final ParkingStrategy parkingStrategy;
    private final PricingStrategy pricingStrategy;
    private final ReentrantLock allocationLock = new ReentrantLock(); // Step 7: Concurrency Safety

    public ParkingLotService(ParkingSlotRepository repo, ParkingStrategy pStrat, PricingStrategy prStrat) {
        this.slotRepository = repo;
        this.parkingStrategy = pStrat;
        this.pricingStrategy = prStrat;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        allocationLock.lock(); // Prevent double booking race conditions
        try {
            List<ParkingSlot> slots = slotRepository.findAllAvailable();
            ParkingSlot slot = parkingStrategy.findSlot(slots, vehicle.getType())
                    .orElseThrow(() -> new IllegalStateException("Parking Full for type: " + vehicle.getType()));
            
            slot.occupy(vehicle);
            slotRepository.update(slot);
            return new Ticket(UUID.randomUUID().toString(), vehicle, slot, Instant.now());
        } finally {
            allocationLock.unlock();
        }
    }
}
```

---

## 🎯 Interview Evaluation Matrix

| Category | What Interviewers Look For | How to Demonstrate |
|---|---|---|
| **Requirement Gathering** | Did candidate clarify edge constraints & NFRs? | List Functional, Non-Functional, and Hero Use Cases upfront. |
| **Object Modeling & SOLID** | Are domain classes cohesive and loosely coupled? | Layer code (Model, Repo, Service, Controller) and use interfaces. |
| **Design Patterns** | Were patterns applied naturally to solve real problems? | Justify why Strategy, Factory, or Observer was selected. |
| **Edge Cases & Concurrency** | Does the code hold up under multi-threaded scale? | Add explicit `ReentrantLock` guards and boundary validations. |
| **Clean Code & Communication**| Is code readable, modular, and communicative? | Walk through your thought process out loud at every step. |

---

## Java Classes

- **`Vehicle`, `Car`, `Bike` (Domain Entities):** Represent vehicle participants with polymorphism.
- **`ParkingSlot` (Domain Model):** Represents slots with state transitions (`FREE`, `OCCUPIED`).
- **`Ticket` (Domain Entity):** Captures entry timestamp and assigned slot metadata.
- **`ParkingStrategy` & `NearestSlotStrategy`:** Strategy pattern selecting optimal parking slots.
- **`PricingStrategy` & `HourlyPricingStrategy`:** Strategy pattern computing fees based on vehicle type and duration.
- **`ParkingLotService`:** Core orchestrator coordinating thread-safe slot allocation and exit settlement.
- **`LLDInterviewFrameworkExample` (Main Driver):** Simulates an end-to-end interview design walk-through.

---

## LLD Takeaway

Mastering the **9-Step Strategic LLD Framework** provides a battle-tested mental template for acing Low-Level Design rounds across Tier-1 tech companies (Google, Microsoft, Amazon, Uber, Swiggy, Razorpay).

---

## 🎯 Quick Summary

- **Core Idea:** Navigate LLD interviews systematically using a 9-step progression: Requirements $\rightarrow$ Entities $\rightarrow$ SOLID/OOP $\rightarrow$ Design Patterns $\rightarrow$ Concurrency $\rightarrow$ Extensibility.
- **Code Demonstrates:** A complete, thread-safe, extensible Smart Parking Lot system designed according to the 9-Step Framework.
- **LLD Takeaway:** Never jump straight to coding; align on requirements, justify design pattern choices, and protect shared state with locks.
- **Memorable Rule:** *"Clarify first, model domain entities second, apply SOLID and design patterns third, and lock down concurrency fourth."*
