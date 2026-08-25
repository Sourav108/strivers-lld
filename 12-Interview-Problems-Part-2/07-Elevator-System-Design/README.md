# Elevator System - Low-Level Design

## 1. Problem Statement

Design a high-throughput, multi-car **Elevator Control System** for a modern skyscraper supporting external floor panel requests (UP/DOWN buttons), internal elevator cabin panel requests (destination floor selection), pluggable dispatching algorithms (**Nearest Elevator**, **Load Balancing**), pluggable cabin movement algorithms (**SCAN/LOOK**, **FCFS**), strict safety state machines (door operations, motion locking), and administrative maintenance & graceful shutdown workflows.

---

## 2. Requirements

### Functional Requirements
- **Multi-Floor & Multi-Elevator Management:** Support $N$ floors (min floor to max floor) and $M$ elevator cars per building.
- **External Floor Panel Requests:** Passengers at any floor can press UP/DOWN buttons to request an elevator.
- **Internal Cabin Panel Requests:** Passengers inside an elevator can select one or more destination floors.
- **Pluggable Dispatching Strategy:** Dispatch the optimal elevator based on proximity, direction, and passenger load (`ElevatorSelectionStrategy`).
- **Pluggable Movement Strategy:** Calculate elevator stops efficiently (`SCAN` algorithm vs `FCFS`).
- **Door & Motion Safety:** Passengers can only enter/exit when doors are fully open; doors cannot open while moving.
- **Maintenance & Capacity Management:** Elevators can enter maintenance mode or signal capacity limits to reject additional boarding.

### Important Non-Functional Requirements
- **Safety State Integrity:** State transitions enforced through the **State Pattern** (`STOPPED` $\rightarrow$ `DOORS_OPENING` $\rightarrow$ `DOORS_CLOSING` $\rightarrow$ `MOVING` $\rightarrow$ `STOPPED` and `MAINTENANCE`).
- **Thread Safety & Concurrent Queuing:** Concurrent requests handled via thread-safe `BlockingQueue` and `ConcurrentHashMap`.
- **Graceful Lifecycle Management:** Drains and completes in-flight requests during system shutdown.

---

## 3. Package Structure

```
src/
├── controller/
│   ├── ElevatorController.java
│   ├── ElevatorPanelController.java
│   └── FloorPanelController.java
├── domain/
│   ├── state/
│   │   ├── DoorsClosingState.java
│   │   ├── DoorsOpeningState.java
│   │   ├── ElevatorStateHandler.java (State Interface)
│   │   ├── MaintenanceState.java
│   │   ├── MovingState.java
│   │   ├── PreMaintenanceState.java
│   │   └── StoppedState.java
│   ├── strategy/
│   │   ├── ElevatorSelectionStrategy.java (Selection Strategy Interface)
│   │   ├── FCFSStrategy.java
│   │   ├── LoadBalancingStrategy.java
│   │   ├── MovementStrategy.java          (Movement Strategy Interface)
│   │   ├── NearestElevatorStrategy.java
│   │   └── ScanStrategy.java
│   ├── Building.java
│   ├── Direction.java                (Enum: UP, DOWN, IDLE)
│   ├── Elevator.java                 (Core Entity)
│   ├── ElevatorState.java            (Enum: MOVING, STOPPED, DOORS_OPENING, DOORS_CLOSING, MAINTENANCE)
│   ├── ExternalRequest.java          (Floor Panel Request)
│   ├── InternalRequest.java          (Elevator Panel Request)
│   ├── RequestStatus.java            (Enum: PENDING, ASSIGNED, COMPLETED, QUEUED)
│   └── SystemState.java              (Enum: RUNNING, STOPPING, STOPPED, MAINTENANCE)
├── repository/
│   ├── impl/
│   │   ├── BuildingRepositoryImpl.java
│   │   ├── ElevatorRepositoryImpl.java
│   │   ├── ExternalRequestRepositoryImpl.java
│   │   └── InternalRequestRepositoryImpl.java
│   ├── BuildingRepository.java        (Interface)
│   ├── ElevatorRepository.java        (Interface)
│   ├── ExternalRequestRepository.java (Interface)
│   └── InternalRequestRepository.java (Interface)
├── service/
│   ├── BuildingService.java
│   ├── DispatcherService.java
│   ├── ElevatorSchedulerService.java
│   ├── ElevatorService.java
│   ├── MovementService.java
│   └── RequestService.java
└── main/
    └── ElevatorSystemSimulation.java (Driver Simulation)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain` | **`Elevator`** | Represents an elevator car with floor position, direction, load capacity, stops, and current state. |
| `domain` | **`Building`** | Manages building floor constraints (`minFloor`, `maxFloor`), elevator fleet, and system state. |
| `domain` | **`ExternalRequest`** | Represents a hall call made from a floor panel (`floorNumber`, `direction`). |
| `domain` | **`InternalRequest`** | Represents a car call made inside an elevator (`destinationFloor`). |
| `domain.state` | **`ElevatorStateHandler`** | State interface defining legal door and movement operations. |
| `domain.state` | **`MovingState`**, **`StoppedState`**, etc. | Concrete state implementations preventing safety violations. |
| `domain.strategy` | **`ElevatorSelectionStrategy`** | Strategy interface for selecting the optimal elevator for a hall call. |
| `domain.strategy` | **`NearestElevatorStrategy`** | Selects elevator based on proximity, directional alignment, and load. |
| `domain.strategy` | **`LoadBalancingStrategy`** | Selects the elevator with minimum passenger load. |
| `domain.strategy` | **`MovementStrategy`** | Strategy interface for sequencing target floor stops. |
| `domain.strategy` | **`ScanStrategy`** | SCAN / LOOK algorithm continuing in current direction before reversing. |
| `service` | **`DispatcherService`** | Thread-safe external request queuing and optimal car assignment. |
| `service` | **`MovementService`** | Handles elevator floor stepping, arrival detection, passenger boarding/alighting, and door cycles. |
| `service` | **`ElevatorSchedulerService`** | Background task runner managing periodic dispatching and movement ticks. |
| `service` | **`ElevatorService`** | Manages elevator creation, maintenance modes, and floor telemetry. |
| `controller` | **`*Controller`** | Entrypoint endpoints for building admin, floor buttons, and internal panels. |
| `main` | **`ElevatorSystemSimulation`** | End-to-end runnable demonstration verifying all scenarios and edge cases. |

---

## 5. Design Patterns & SOLID Principles

- **State Pattern:**
  - `ElevatorStateHandler` encapsulates elevator door and motion state logic.
  - Prevents dangerous actions (e.g., opening doors while moving, moving with open doors).
- **Strategy Pattern (Dual Implementations):**
  - **Elevator Selection:** Pluggable `ElevatorSelectionStrategy` (`NearestElevatorStrategy`, `LoadBalancingStrategy`).
  - **Path Planning:** Pluggable `MovementStrategy` (`ScanStrategy`, `FCFSStrategy`).
- **Single Responsibility Principle (SRP):**
  - `DispatcherService` only matches requests to elevators; `MovementService` only handles physical movement and stop servicing; `ElevatorService` manages car hardware lifecycle.
- **Open/Closed Principle (OCP):**
  - New dispatching algorithms (e.g. Energy-Saving Strategy) or movement algorithms (e.g. Shortest Seek Time First) can be added without modifying existing services.

---

## 6. Main Flows

### Flow 1: Hall Call (External Request) & Dispatch
```
1. Passenger at Floor 4 presses UP:
   FloorPanelController.pressUpButton(4, bldgId)
   -> RequestService creates ExternalRequest(Floor: 4, UP)
   -> DispatcherService queues request in thread-safe BlockingQueue

2. Dispatcher Tick:
   DispatcherService.processPendingRequests()
   -> Evaluates available elevators via NearestElevatorStrategy
   -> Calculates proximity + directional alignment + load
   -> Assigns Floor 4 to Elevator #ELEV-2 (currently at Floor 5)
   -> Elevator #ELEV-2 adds Floor 4 to its target stops
```

### Flow 2: Movement, Arrival & Boarding
```
1. Movement Tick:
   MovementService.processElevatorMovement(ELEV-2)
   -> ScanStrategy calculates next stop: Floor 4
   -> ELEV-2 moves DOWN to Floor 4
   -> Arrival: ELEV-2 stops -> Doors OPEN -> Passenger boards (Load: 1) -> Doors CLOSE
   -> ExternalRequest marked COMPLETED
```

---

## 7. Edge Cases Handled

1. **Safety Motion Interlock:** Doors cannot be opened while in `MovingState`; movement cannot start while in `DoorsOpeningState`.
2. **Elevator Reaches Full Capacity:** `isFull()` check prevents `DispatcherService` from assigning new external requests to saturated elevators.
3. **Elevator Maintenance Mode:** Entering maintenance sets `isActive = false`, finishes pending drop-offs, and prevents new hall calls from being assigned.
4. **Equidistant Conflict Resolution:** `NearestElevatorStrategy` breaks distance ties using directional affinity and lower passenger load.
5. **System Graceful Shutdown:** Setting state to `STOPPING` drains pending requests before terminating the background executor.

---

## 8. How to Run

Compile and execute from the `07-Elevator-System-Design` directory:

```bash
# Compile all packaged Java sources
javac -d bin $(find src -name "*.java")

# Run the complete demonstration driver
java -cp bin main.ElevatorSystemSimulation
```

---

## 9. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Building dimensions (floors, elevators) $\rightarrow$ Floor panels (UP/DOWN) $\rightarrow$ Car panels (Floor selection) $\rightarrow$ Dispatching vs Movement algorithms $\rightarrow$ Maintenance & safety states.
2. **Step 2 (Identify Core Entities):** `Building`, `Elevator`, `ExternalRequest`, `InternalRequest`, `ElevatorStateHandler`.
3. **Step 3 (Select Key Design Patterns):**
   - **State Pattern** for door and movement safety.
   - **Strategy Pattern (x2)** for dispatching (`Nearest` vs `LoadBalancing`) and motion planning (`SCAN` vs `FCFS`).
4. **Step 4 (Explain SCAN Algorithm):** Detail how the elevator serves all requests in the current direction before sweeping back in the reverse direction.

### Likely Interviewer Follow-up Questions
1. *Why separate `ElevatorSelectionStrategy` from `MovementStrategy`?*
   - **Answer:** Selection is a **global fleet optimization problem** (which car should pick up the passenger), whereas Movement is a **local single-car routing problem** (how should a single car visit its set of assigned stops).
2. *How do you prevent starvation for opposite-direction requests in SCAN?*
   - **Answer:** Use LOOK algorithm with sweep bounds so the elevator reverses once the furthest request in the current direction is reached, immediately servicing opposite-direction queues.

---

## 🎯 Quick Summary

- **Problem:** Design a robust multi-elevator system with floor/car panels, optimal dispatching, SCAN movement, and safety state handling.
- **Core Classes:** `Elevator`, `Building`, `ElevatorStateHandler` (`StoppedState`, `MovingState`, etc.), `DispatcherService`, `MovementService`, `NearestElevatorStrategy`, `ScanStrategy`.
- **Main Flow:** Floor Panel Button $\rightarrow$ Dispatcher assigns Car via Strategy $\rightarrow$ Car moves to Floor $\rightarrow$ Doors Open/Close $\rightarrow$ Destination selected $\rightarrow$ SCAN Path traversal.
- **Important Design:** State Pattern for mechanical safety; Dual Strategy Pattern for dispatching & motion planning; Non-blocking request queues.
- **Edge Cases:** Full capacity bypass, maintenance mode draining, motion interlock, equidistant tie-breaking, and graceful shutdown.
- **LLD Takeaway:** Separate global fleet dispatching from local car motion planning, and enforce mechanical invariants strictly with the State pattern.
- **Memorable Rule:** *"Selection finds the car, SCAN plans the path, and State protects the passenger."*
