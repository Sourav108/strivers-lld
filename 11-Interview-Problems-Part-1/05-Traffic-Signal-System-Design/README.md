# Traffic Signal System - Low-Level Design

## 1. Problem Statement

Design a robust, real-time, and safety-critical **Traffic Signal System** for a 4-way intersection (`NORTH`, `EAST`, `SOUTH`, `WEST`). The system must enforce valid signal transitions (`RED` $\rightarrow$ `GREEN` $\rightarrow$ `YELLOW` $\rightarrow$ `RED`), cycle through directional phases sequentially, support immediate **Emergency Vehicle Priority Overrides** (pausing normal cycles and granting green lights), and dynamically adjust green signal durations based on vehicle density sensors.

---

## 2. Requirements

### Functional Requirements
- **Single 4-Way Intersection:** Coordinate 4 traffic lights (`NORTH`, `EAST`, `SOUTH`, `WEST`) as a cohesive unit.
- **Sequential Phase Cycling:** Automatic round-robin phase progression (`NORTH` $\rightarrow$ `EAST` $\rightarrow$ `SOUTH` $\rightarrow$ `WEST`).
- **Strict State Transitions:** `RED` $\rightarrow$ `GREEN` $\rightarrow$ `YELLOW` $\rightarrow$ `RED`. Invalid jumps (e.g. `RED` $\rightarrow$ `YELLOW` or `GREEN` $\rightarrow$ `RED`) must be blocked by design.
- **Emergency Vehicle Priority Override:**
  - Pause the active automatic cycle.
  - Safely transition all signals to `RED`.
  - Grant priority `GREEN` to the emergency direction.
  - Upon clearance, safely return to `RED` and resume the normal cycle from the exact paused phase.
- **Vehicle Density Detection & Dynamic Timing:** Adjust green light durations dynamically based on vehicle count sensors (e.g., 10s for light traffic vs 45s for congested traffic).

### Important Non-Functional Requirements
- **Safety & Zero Ambiguity:** Prevent conflicting green signals active simultaneously across crossing directions.
- **State Integrity:** All light state changes must pass through valid transitions to avoid sudden light cutoffs.
- **Extensibility:** Support multi-intersection networking and pedestrian crossings.

---

## 3. Package Structure

```
src/
├── controller/
│   ├── EmergencyController.java
│   ├── IntersectionController.java
│   ├── TimingController.java
│   └── TrafficController.java
├── domain/
│   ├── state/
│   │   ├── TrafficLightState.java (Interface)
│   │   ├── RedState.java
│   │   ├── GreenState.java
│   │   ├── YellowState.java
│   │   ├── OffState.java
│   │   └── InvalidStateTransitionException.java
│   ├── Direction.java             (Enum)
│   ├── EmergencyRequest.java
│   ├── Intersection.java
│   ├── IntersectionCycle.java
│   ├── SignalTiming.java
│   ├── TrafficLight.java
│   └── VehicleCounter.java
├── repository/
│   ├── EmergencyRepository.java
│   ├── IntersectionRepository.java
│   ├── TimingRepository.java
│   └── TrafficRepository.java
├── service/
│   ├── EmergencyService.java
│   ├── IntersectionService.java
│   ├── TimingService.java
│   └── TrafficService.java
└── TrafficSignalSystem.java       (Main Driver)
```

---

## 4. Class Responsibilities

| Package | Class / Interface | Responsibility (1 Line) |
|---|---|---|
| `domain.state` | **`TrafficLightState`** | State pattern interface declaring transitions (`turnGreen`, `turnYellow`, `turnRed`, `turnOff`). |
| `domain.state` | **`RedState`** | Allows transition to `GreenState` / `OffState`; throws `InvalidStateTransitionException` on `turnYellow`. |
| `domain.state` | **`GreenState`** | Allows transition to `YellowState` / `OffState`; throws `InvalidStateTransitionException` on `turnRed`. |
| `domain.state` | **`YellowState`** | Allows transition to `RedState` / `OffState`; throws `InvalidStateTransitionException` on `turnGreen`. |
| `domain.state` | **`OffState`** | Inactive maintenance state that allows transitioning to any active state. |
| `domain` | **`Direction`** | Enum defining the 4 cardinal directions (`NORTH`, `EAST`, `SOUTH`, `WEST`). |
| `domain` | **`TrafficLight`** | Context managing current state and enforcing valid transitions via the State Pattern. |
| `domain` | **`SignalTiming`** | Holds duration configurations (green duration, yellow 3s constant, dynamic flag). |
| `domain` | **`VehicleCounter`** | Thread-safe accumulator tracking real-time vehicle density per direction. |
| `domain` | **`EmergencyRequest`** | Represents an emergency vehicle priority override event. |
| `domain` | **`IntersectionCycle`** | Manages phase index progression and pause/resume checkpoints. |
| `domain` | **`Intersection`** | Aggregate root managing lights, timings, counters, and emergency flags. |
| `repository` | **`EmergencyRepository`** | In-memory persistence for active emergency requests. |
| `repository` | **`IntersectionRepository`** | In-memory persistence for intersections. |
| `repository` | **`TimingRepository`** | In-memory storage for direction signal timings. |
| `repository` | **`TrafficRepository`** | In-memory storage for vehicle density counters. |
| `service` | **`IntersectionService`** | Orchestrates coordinated light switching for normal automatic phase cycles. |
| `service` | **`EmergencyService`** | Manages emergency priority overrides, cycle pausing, and safe restoration. |
| `service` | **`TimingService`** | Dynamically calculates optimal green light timings based on vehicle counts. |
| `service` | **`TrafficService`** | Updates sensor counts and tracks traffic congestion per approach. |
| `controller` | **`EmergencyController`** | Entry point exposing emergency trigger and status APIs. |
| `controller` | **`IntersectionController`** | Entry point exposing intersection creation and phase advancement APIs. |
| `controller` | **`TimingController`** | Entry point for configuring static and dynamic signal timings. |
| `controller` | **`TrafficController`** | Entry point for sensor density count ingestion. |
| Root | **`TrafficSignalSystem`** | Main simulation driver verifying all scenarios, transitions, and edge cases. |

---

## 5. Design Patterns & SOLID Principles

- **State Pattern:**
  - `TrafficLightState` hierarchy (`RedState`, `GreenState`, `YellowState`, `OffState`) encapsulates valid state machine logic and transitions.
- **Repository Pattern:**
  - `IntersectionRepository`, `EmergencyRepository`, `TimingRepository`, `TrafficRepository` decouple business logic from storage.
- **Layered Architecture:**
  - Strict separation of concerns between `controller`, `service`, `repository`, and `domain` layers.
- **Single Responsibility Principle (SRP):**
  - Each class is focused on one concern: `TrafficLightState` for state validation, `EmergencyService` for emergency overrides, and `TimingService` for duration algorithms.
- **Open/Closed Principle (OCP):**
  - New states (e.g. `FlashingYellowState`) or dynamic timing algorithms can be added without modifying existing core classes.

---

## 6. Main Flows

### Flow 1: Normal Phase Advance
```
IntersectionController.advancePhase(intersectionId, EAST)
  -> IntersectionService checks if emergencyMode is active (false)
  -> Finds active GREEN signal (NORTH)
  -> NORTH.turnYellow() -> [NORTH: YELLOW]
  -> NORTH.turnRed()    -> [NORTH: RED]
  -> EAST.turnGreen()   -> [EAST: GREEN] (Active for configured greenDuration)
```

### Flow 2: Emergency Vehicle Priority Override
```
EmergencyController.requestEmergency(intersectionId, WEST, 15s)
  -> EmergencyService.requestEmergency()
  -> IntersectionCycle.pause() (Saves active phase checkpoint)
  -> Intersection.setEmergencyMode(true, WEST)
  -> Intersection.setAllSignalsToRed()
  -> WEST.turnGreen() -> [WEST: GREEN granted for 15s]
... (Ambulance clears intersection) ...
EmergencyController.endEmergency(intersectionId)
  -> WEST.emergencyTransitionToRed() -> [WEST: RED]
  -> Intersection.setEmergencyMode(false, null)
  -> IntersectionCycle.resume()
  -> Resumes normal cycle from saved phase!
```

---

## 7. Edge Cases Handled

1. **Emergency Request during Active Green:** The currently green signal transitions through `YELLOW` to `RED` before emergency green is granted.
2. **Invalid State Transition:** Attempting to force `RED` to `YELLOW` throws `InvalidStateTransitionException` preventing road accidents.
3. **Cycle Resume Accuracy:** The system remembers exactly which phase was interrupted and resumes without restarting from phase 0.
4. **Traffic Congestion Spikes:** `TimingService` bounds dynamic green durations between safety limits (5s minimum, 120s maximum).

---

## 8. How to Run

Compile and execute from the `05-Traffic-Signal-System-Design` directory:

```bash
# Compile all packaged Java sources
javac -d bin $(find src -name "*.java")

# Run the complete demonstration
java -cp bin TrafficSignalSystem
```

---

## 🎯 Quick Summary

- **Problem:** Design a 4-way traffic signal system with safety-enforced state transitions, emergency priority overrides, and dynamic timing.
- **Core Classes:** `Intersection`, `TrafficLight`, `TrafficLightState` (`RedState`, `GreenState`, `YellowState`), `IntersectionCycle`, `EmergencyService`.
- **Main Flow:** `advancePhase()` $\rightarrow$ Active `GREEN` $\rightarrow$ `YELLOW` $\rightarrow$ `RED` $\rightarrow$ Next Direction `GREEN`.
- **Important Design:** State Pattern for transition validation; Cycle Pause/Resume for emergency handling.
- **Edge Cases:** Invalid transition traps (`InvalidStateTransitionException`), emergency override during phase changes, and bounded dynamic timing (5s–120s).
- **LLD Takeaway:** Never manage safety-critical state machines with raw strings or loose flags; enforce invariants using the State Pattern.
- **Memorable Rule:** *"Transition through Yellow, lock down crossing greens, and pause/resume for emergencies."*
