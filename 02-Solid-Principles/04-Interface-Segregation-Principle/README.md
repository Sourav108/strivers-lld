# 04 - Interface Segregation Principle (ISP)

> **Definition:** Clients should not be forced to depend upon interfaces that they do not use.
> 
> *"Many client-specific interfaces are better than one general-purpose fat interface."*

---

## 💡 Real-Life Analogy

### 🚗 The Uber App Experience
Imagine opening your Uber app as a **passenger (rider)**:
- You only need features to **book rides, track your driver, pay, and rate the driver**.
- You do **not** want or need features to **accept ride requests, verify commercial driving licenses, inspect passenger profiles, or track daily earnings**.
- If the Uber app gave riders and drivers the exact same cluttered screen with all buttons enabled, it would be confusing, error-prone, and fragile.

ISP ensures that riders see only rider features, and drivers see only driver features.

---

## ❌ Bad Design (Violating ISP: "Fat / Monolithic Interface")

A single monolithic interface forced onto all types of users:

```java
// ❌ Monolithic "Fat" Interface
interface UberUser {
    void bookRide();
    void acceptRide();
    void trackEarnings();
    void ratePassenger();
    void rateDriver();
}
```

### Why this is problematic:
```java
class Rider implements UberUser {
    public void bookRide() { System.out.println("Booking ride..."); }
    public void rateDriver() { System.out.println("Rating driver 5 stars..."); }

    // ⚠️ FORCED DUMMY IMPLEMENTATIONS (Polluting the class):
    public void acceptRide() { throw new UnsupportedOperationException("Riders cannot accept rides!"); }
    public void trackEarnings() { throw new UnsupportedOperationException("Riders do not have driver earnings!"); }
    public void ratePassenger() { throw new UnsupportedOperationException("Riders cannot rate passengers!"); }
}
```
- **Interface Pollution:** Implementing classes are littered with blank methods or `UnsupportedOperationException`.
- **Fragile Coupling:** Changing a driver-specific method signature forces recompilation and testing across all rider classes.
- **Security / Accidental Misuse:** Exposes internal driver APIs to rider contexts.

---

## ✅ Good Design (Adhering to ISP: Segregated Interfaces)

Break the fat interface into lean, client-specific contracts:

```
                         ┌─────────────────────────┐
                         │     RiderOperations     │
                         ├─────────────────────────┤
                         │ + bookRide()            │
                         │ + rateDriver(rating)    │
                         └────────────▲────────────┘
                                      │
                         ┌────────────┴────────────┐
                         │          Rider          │
                         └─────────────────────────┘

                         ┌─────────────────────────┐
                         │    DriverOperations     │
                         ├─────────────────────────┤
                         │ + acceptRide(rideId)    │
                         │ + trackEarnings()       │
                         │ + ratePassenger(rating) │
                         └────────────▲────────────┘
                                      │
                         ┌────────────┴────────────┐
                         │         Driver          │
                         └─────────────────────────┘
```

### Implementation:

```java
// Lean, role-specific interfaces
interface RiderOperations {
    void bookRide(String pickup, String destination);
    void rateDriver(int rating);
}

interface DriverOperations {
    void acceptRide(String rideId);
    void trackEarnings();
    void ratePassenger(int rating);
}

// Classes only implement methods they actually use
class Rider implements RiderOperations {
    @Override
    public void bookRide(String pickup, String destination) {
        System.out.println("🚖 [Rider] Booked ride from " + pickup + " to " + destination);
    }

    @Override
    public void rateDriver(int rating) {
        System.out.println("⭐ [Rider] Rated driver " + rating + "/5 stars.");
    }
}

class Driver implements DriverOperations {
    @Override
    public void acceptRide(String rideId) {
        System.out.println("✅ [Driver] Accepted ride request: " + rideId);
    }

    @Override
    public void trackEarnings() {
        System.out.println("💰 [Driver] Current earnings: $145.50");
    }

    @Override
    public void ratePassenger(int rating) {
        System.out.println("⭐ [Driver] Rated passenger " + rating + "/5 stars.");
    }
}
```

---

## 🚀 Key Advantages of ISP

1. **Cleaner Codebase:** Classes are not bloated with dummy, no-op, or exception-throwing methods.
2. **High Maintainability & Isolation:** Modifying driver features never affects or triggers recompilation of rider classes.
3. **Seamless Scalability:** Adding new roles (e.g. `DeliveryPartner` for Uber Eats) is clean and decoupled:
   ```java
   interface DeliveryOperations {
       void pickUpOrder(String orderId);
       void deliverOrder(String orderId);
   }
   ```
4. **Flexible Composition:** A user who is both a Rider and Driver can implement multiple segregated interfaces (`class PowerUser implements RiderOperations, DriverOperations`).

---

## 🎯 When to Apply ISP?

- When you notice classes implementing interfaces with empty/no-op methods or throwing `UnsupportedOperationException`.
- When an interface starts accumulating responsibilities used by only a subset of implementers.
- When changes to one feature force unrelated classes to update or recompile.
- When designing public APIs, SDKs, or microservice client libraries where clients should only see relevant endpoints.

> [!TIP]
> **Avoid Micro-Interfaces Overkill:** Do not create a single-method interface for every method unless there is a valid reason. Group methods that naturally cohere to a specific role/client.

---

### 🎯 Quick Summary

* **Core Idea:** Clients should never be forced to depend on methods or interfaces they do not use.
* **Code Demonstrates:** Refactoring a bloated `UberUser` interface into segregated `RiderOperations` and `DriverOperations` interfaces.
* **LLD Takeaway:** Design small, client-specific interfaces so classes implement only the behaviors relevant to their role.
* **Memorable Rule:** *"Fat interfaces lead to bloated classes; keep interfaces lean, focused, and role-specific."*
