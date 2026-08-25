# Hotel Management System - Low-Level Design

## 1. Problem Statement

Design a comprehensive, scalable, and inventory-safe **Hotel Management & Room Reservation System** supporting real-time multi-criteria search, dynamic seasonal & surge pricing, two-phase booking workflows (`CREATED` $\rightarrow$ `HELD` $\rightarrow$ `CONFIRMED` $\rightarrow$ `CHECKED_IN` $\rightarrow$ `CHECKED_OUT`), overbooking tolerance, policy-driven cancellations with automated refunds, and guest room allocation.

---

## 2. Requirements

### Functional Requirements
- **Hotel & Room Search:** Search and browse hotels by city, country, and stay date range (`[check-in, check-out)`).
- **Real-Time Dynamic Availability & Pricing:** Query real-time available room capacity per night and compute dynamic stay pricing (base rates + seasonal surge overrides).
- **Two-Phase Booking Workflow:**
  - *Phase 1 (Price Lock):* Creates `CREATED` booking locking nightly prices without blocking inventory.
  - *Phase 2 (Payment Hold):* Payment initiation transitions booking to `HELD`, locking room inventory for a 10-minute TTL.
  - *Confirmation:* Gateway success callback confirms booking (`CONFIRMED`).
- **Check-in & Check-out Lifecycle:**
  - *Check-in:* Allocates physical room number to confirmed booking (`CHECKED_IN`).
  - *Check-out:* Marks booking `CHECKED_OUT` and releases inventory immediately.
- **Cancellation Policy & Refunds:** Evaluates cancellation requests against hotel cancellation policies (`NON_REFUNDABLE`, `PARTIAL`, `FLEXIBLE`) based on cutoff hours before check-in.
- **Admin Control Panel:** Manage hotels, room types, physical rooms, seasonal prices, and overbooking allowances.

### Important Non-Functional Requirements
- **Inventory Safety & Overbooking Caps:** Calculate availability safely taking configurable overbooking buffers into account (`totalRooms + overbookCap - activeBookings`).
- **State Machine Integrity:** Enforce strict state transitions via `BookingStateHandler` to avoid race conditions or invalid skips.
- **Monetary Precision:** All amounts stored in minor units (paisa/cents as `long`).

---

## 3. Package Structure

```
src/
├── controller/
│   ├── AdminController.java
│   ├── BookingController.java
│   ├── DashboardController.java
│   ├── SearchController.java
│   └── TransactionController.java
├── domain/
│   ├── Availability.java
│   ├── Booking.java
│   ├── BookingStatus.java            (Enum: CREATED, HELD, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED)
│   ├── CancellationPolicy.java
│   ├── DateRange.java
│   ├── Hotel.java
│   ├── NightlyPrice.java
│   ├── RefundDecision.java
│   ├── Room.java
│   ├── RoomType.java
│   ├── RoomTypeAvailability.java
│   ├── SearchFilter.java
│   ├── SeasonalPrice.java
│   ├── Transaction.java
│   ├── TransactionStatus.java        (Enum: PENDING, COMPLETED, REFUNDED, FAILED)
│   ├── User.java
│   └── UserRole.java                 (Enum: CUSTOMER, ADMIN)
├── repository/
│   ├── impl/
│   │   ├── BookingRepositoryImpl.java
│   │   ├── CancellationPolicyRepositoryImpl.java
│   │   ├── HotelRepositoryImpl.java
│   │   ├── RoomRepositoryImpl.java
│   │   ├── RoomTypeRepositoryImpl.java
│   │   ├── SeasonalPriceRepositoryImpl.java
│   │   ├── TransactionRepositoryImpl.java
│   │   └── UserRepositoryImpl.java
│   ├── BookingRepository.java        (Interface)
│   ├── CancellationPolicyRepository.java (Interface)
│   ├── HotelRepository.java          (Interface)
│   ├── RoomRepository.java           (Interface)
│   ├── RoomTypeRepository.java       (Interface)
│   ├── SeasonalPriceRepository.java  (Interface)
│   ├── TransactionRepository.java    (Interface)
│   └── UserRepository.java           (Interface)
├── service/
│   ├── BookingService.java
│   ├── BookingStateHandler.java      (State Machine Manager)
│   ├── InventoryService.java
│   ├── PolicyService.java
│   ├── PricingService.java
│   ├── SearchService.java
│   ├── TransactionService.java
│   └── UserService.java
└── main/
    └── HotelManagementSimulation.java (Driver Simulation)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain` | **`Hotel`** | Aggregate root managing hotel details, rating, and overbooking allowances. |
| `domain` | **`RoomType`** | Categorizes rooms (Deluxe, Suite), capacity, bed type, base pricing, and total count. |
| `domain` | **`Room`** | Physical hotel room with a unique room number. |
| `domain` | **`Booking`** | Reservation entity holding stay date range, locked nightly rates, and status. |
| `domain` | **`SeasonalPrice`** | Date-specific price override for dynamic surge/holiday rates. |
| `domain` | **`CancellationPolicy`** | Defines refund percentages and cutoff hour thresholds before check-in. |
| `domain` | **`DateRange`** | Encapsulates check-in/check-out boundaries and date slice generation. |
| `domain` | **`RefundDecision`** | Value object holding refund eligibility, percentage, and calculated refund amount. |
| `service` | **`BookingStateHandler`** | Central state machine enforcing legal booking lifecycle transitions. |
| `service` | **`InventoryService`** | Calculates net available room capacity factoring overbooking percent. |
| `service` | **`PricingService`** | Computes nightly rates, total cost, and average daily rate across a stay. |
| `service` | **`PolicyService`** | Evaluates cancellation timing against hotel policy to determine refunds. |
| `service` | **`BookingService`** | Orchestrates 2-phase booking creation, cancellation, check-in, and check-out. |
| `service` | **`TransactionService`** | Handles payment hold creation, webhook completion callbacks, and refunds. |
| `service` | **`SearchService`** | Handles hotel discovery and real-time room availability calculation. |
| `controller` | **`*Controller`** | REST endpoints delegating requests across services. |
| `main` | **`HotelManagementSimulation`** | Simulation driver verifying search, booking, payment, check-in, and refunds. |

---

## 5. Design Patterns & SOLID Principles

- **State Pattern (`BookingStateHandler`):**
  - Encapsulates booking lifecycle transitions (`CREATED` $\rightarrow$ `HELD` $\rightarrow$ `CONFIRMED` $\rightarrow$ `CHECKED_IN` $\rightarrow$ `CHECKED_OUT`), preventing illegal jumps (e.g., checking in before payment confirmation).
- **Two-Phase Inventory Lock:**
  - Separates booking initialization (`CREATED`) from inventory commitment (`HELD`), preventing abandoned carts from hoarding inventory.
- **Repository Pattern:**
  - Abstracts in-memory data access across hotels, bookings, room types, and seasonal pricing.
- **Single Responsibility Principle (SRP):**
  - `PricingService` focuses strictly on dynamic daily rates; `InventoryService` handles room counting & overbooking; `PolicyService` evaluates refunds.
- **Open/Closed Principle (OCP):**
  - New pricing strategies or dynamic cancellation policy tiers can be plugged in without modifying core booking logic.

---

## 6. Main Flows

### Flow 1: Two-Phase Booking Workflow
```
1. Search & Rate:
   SearchController.getAvailability(hotelId, [2026-08-30 to 2026-09-01])
   -> PricingService evaluates: Night 1 (Base ₹4,000) + Night 2 (Surge ₹5,500) = ₹9,500

2. Phase 1 (Create Booking & Price Lock):
   BookingController.createBooking(..., expectedTotal: ₹9,500)
   -> Pre-checks inventory (Available >= 1)
   -> Creates Booking(status = CREATED) with locked nightly price list

3. Phase 2 (Initiate Payment & Lock Inventory):
   TransactionController.initiateTransaction(booking)
   -> Booking status transitions: CREATED -> HELD
   -> Inventory is now blocked for 10-min TTL

4. Gateway Callback:
   TransactionController.handleTransactionCallback(providerRef, COMPLETED)
   -> Booking status transitions: HELD -> CONFIRMED
```

### Flow 2: Cancellation & Policy-Driven Refund
```
BookingController.cancelBooking(bookingId, userId, cancellationDate)
  -> PolicyService compares cancellationDate with checkInDate
  -> 4 days before check-in >= 48 hours cutoff window
  -> Flexible Policy awards 80% refund (₹7,600 out of ₹9,500)
  -> Booking status transitions: CONFIRMED -> CANCELLED
  -> TransactionService issues refund of ₹7,600
  -> Inventory auto-restored for all stay dates
```

---

## 7. Edge Cases Handled

1. **Price Drift During Checkout:** If hotel prices update between search and click, `createBooking()` detects mismatch against `expectedTotalPrice` and rejects stale rates.
2. **Double-Booking & Overbooking Limits:** `InventoryService` computes `maxCapacity = totalRooms + (totalRooms * overbookPercent / 100) - activeBookings`, strictly rejecting holds when capacity is reached.
3. **Late / Expired Payment Holds:** Background workers drain expired `HELD` bookings (`holdExpiresAt < now`), reverting them to `CANCELLED` and freeing inventory.
4. **Early Check-Out:** When a guest checks out early, `checkOut()` sets `CHECKED_OUT`, releasing remaining nights back to inventory immediately.
5. **Cancellations Past Cutoff Window:** Evaluated by `PolicyService`; cancellations within cutoff receive 0% refund while safely releasing the room.

---

## 8. How to Run

Compile and execute from the `05-Hotel-Management-System-Design` directory:

```bash
# Compile all packaged Java sources
javac -d bin $(find src -name "*.java")

# Run the complete demonstration driver
java -cp bin main.HotelManagementSimulation
```

---

## 9. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Search by location/dates $\rightarrow$ Real-time availability with dynamic pricing $\rightarrow$ Two-phase booking (Price Lock then Hold) $\rightarrow$ Check-in/Check-out $\rightarrow$ Cancellation policies.
2. **Step 2 (Identify Core Entities):** `Hotel`, `RoomType`, `Room`, `Booking`, `SeasonalPrice`, `CancellationPolicy`, `Transaction`.
3. **Step 3 (Select Key Design Patterns):**
   - **State Pattern / `BookingStateHandler`** for booking workflow transitions.
   - **Two-Phase Lock** to avoid hoarding unsold inventory.
   - **Repository Pattern** for decoupling data access.
4. **Step 4 (Walk Through Edge Cases):** Dynamic surge price calculation, overbooking buffers, and early check-out inventory release.

### Likely Interviewer Follow-up Questions
1. *How would you handle race conditions during flash sale bookings?*
   - **Answer:** Use Redis distributed locks or SQL row-level pessimistic locking (`SELECT ... FOR UPDATE`) on the `(roomTypeId, date)` inventory record when transitioning from `CREATED` $\rightarrow$ `HELD`.
2. *Why book by `RoomType` instead of assigning a specific `Room` upfront?*
   - **Answer:** Booking by `RoomType` maximizes operational flexibility for housekeeping, room upgrades, and maintenance, assigning the physical room number only upon physical check-in.

---

## 🎯 Quick Summary

- **Problem:** Design an end-to-end hotel reservation system with dynamic pricing, two-phase booking, overbooking controls, and cancellation policies.
- **Core Classes:** `Hotel`, `RoomType`, `Room`, `Booking`, `BookingStateHandler`, `InventoryService`, `PricingService`, `PolicyService`.
- **Main Flow:** Search Hotel $\rightarrow$ Check Nightly Rates $\rightarrow$ Create Booking (Price Lock) $\rightarrow$ Hold Inventory & Pay $\rightarrow$ Confirm $\rightarrow$ Check-in $\rightarrow$ Check-out.
- **Important Design:** Two-Phase Booking (`CREATED` $\rightarrow$ `HELD` $\rightarrow$ `CONFIRMED`); State Pattern for status transitions; Dynamic seasonal pricing.
- **Edge Cases:** Price drift protection, overbooking buffer enforcement, hold TTL expiry, early check-out inventory release, and cutoff window refunds.
- **LLD Takeaway:** Decouple room type reservations from physical room allocation and use a two-phase hold mechanism for bulletproof inventory concurrency.
- **Memorable Rule:** *"Lock price on create, lock inventory on hold, confirm on payment, and assign room at check-in."*
