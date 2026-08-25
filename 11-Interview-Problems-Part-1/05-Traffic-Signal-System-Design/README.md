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

## 3. Core Entities

- **`Direction` (Enum):** Represents the 4 intersection approaches: `NORTH`, `EAST`, `SOUTH`, `WEST`.
- **`TrafficLightState` (State Pattern Interface):** Defines transition contracts implemented by `RedState`, `GreenState`, `YellowState`, `OffState`.
- **`TrafficLight` (Context):** Manages current state and enforces valid transitions via the State Pattern.
- **`SignalTiming` (Domain Model):** Stores configurable green/yellow durations and dynamic adjustment flags per direction.
- **`VehicleCounter` (Domain Model):** Tracks vehicle counts per direction detected by road sensors.
- **`EmergencyRequest` (Domain Model):** Tracks active emergency vehicle overrides.
- **`IntersectionCycle` (Domain Model):** Manages phase indices and handles pause/resume state.
- **`Intersection` (Aggregate Root):** Aggregates 4 `TrafficLight` units, `SignalTiming`, `VehicleCounter`, and `IntersectionCycle`.

---

## 4. Main Use Cases

1. **Normal Automatic Cycling:** Step through each direction phase: `GREEN` (e.g., 10s) $\rightarrow$ `YELLOW` (3s) $\rightarrow$ `RED` $\rightarrow$ Next Direction.
2. **Invalid State Transition Prevention:** Attempting to force `RED` $\rightarrow$ `YELLOW` directly throws `InvalidStateTransitionException`.
3. **Emergency Vehicle Priority Override:** Immediate pause of cycle, all signals set to `RED`, emergency lane gets `GREEN`, and normal cycle resumes after emergency passes.
4. **Dynamic Traffic Timing Adjustment:** Increase green duration when road sensors report heavy congestion.

---

## 5. Class Responsibilities

| Class / Interface | Responsibility (1 Line) |
|---|---|
| **`Direction`** | Enum defining the 4 cardinal directions (`NORTH`, `EAST`, `SOUTH`, `WEST`). |
| **`TrafficLightState`** | State pattern interface declaring transition methods (`turnGreen`, `turnYellow`, `turnRed`, `turnOff`). |
| **`RedState`** | Permits transition to `GreenState` or `OffState`; blocks `turnYellow`. |
| **`GreenState`** | Permits transition to `YellowState` or `OffState`; blocks direct `turnRed`. |
| **`YellowState`** | Permits transition to `RedState` or `OffState`; blocks direct `turnGreen`. |
| **`OffState`** | Inactive maintenance state that allows transitioning to any active state. |
| **`TrafficLight`** | State context object holding current state and executing transitions. |
| **`SignalTiming`** | Holds duration configurations (green duration, yellow 3s constant, dynamic flag). |
| **`VehicleCounter`** | Thread-safe accumulator tracking real-time vehicle density per direction. |
| **`EmergencyRequest`** | Represents an emergency vehicle priority override event. |
| **`IntersectionCycle`** | Manages phase index progression and pause/resume checkpoints. |
| **`Intersection`** | Aggregate root managing lights, timings, counters, and emergency flags. |
| **`IntersectionService`**| Orchestrates coordinated light switching for normal automatic phase cycles. |
| **`EmergencyService`** | Manages emergency priority overrides, cycle pausing, and safe restoration. |
| **`TrafficService`** | Updates sensor counts and tracks traffic congestion per approach. |
| **`TimingService`** | Dynamically calculates optimal green light timings based on vehicle counts. |

---

## 6. Class Relationships

```
                                +-----------------------+
                                |  <<Interface>> State  |
                                |   TrafficLightState   |
                                +-----------------------+
                                   /       |       \
                                  /        |        \
                        +------------+ +------------+ +------------+
                        |  RedState  | | GreenState | |YellowState |
                        +------------+ +------------+ +------------+
                                           ^
                                           | State Pattern
                                +-----------------------+
                                |     TrafficLight      |
                                +-----------------------+
                                           ^
                                           | 4 instances
+-----------------------+       +-----------------------+
|  IntersectionService  | ----> |     Intersection      |
|   EmergencyService    |       | (Aggregates Lights,   |
|    TrafficService     |       |  Timings, Counters)   |
|     TimingService     |       +-----------------------+
+-----------------------+                   |
                                            v
                                +-----------------------+
                                |   IntersectionCycle   |
                                | (Pause/Resume Phasing)|
                                +-----------------------+
```

---

## 7. Design

### Important Design Decisions
1. **State Pattern for Traffic Lights:** Eliminates nested `if-else` / `switch` statements and enforces safety invariants: A traffic light cannot jump directly from `GREEN` $\rightarrow$ `RED` without passing through `YELLOW`.
2. **Cycle Pause/Resume State:** `IntersectionCycle` captures the active phase index when an emergency arises, allowing the intersection to resume smoothly from where it left off.
3. **Coordinated Emergency All-Red Transition:** Rather than abruptly cutting power, `emergencyTransitionToRed()` brings `GREEN` lights through `YELLOW` to `RED` safely before granting the emergency green.

### SOLID Principles
- **SRP (Single Responsibility):** `TrafficLightState` handles state validation; `EmergencyService` manages priority overrides; `TimingService` manages duration calculations.
- **OCP (Open/Closed Principle):** New states (e.g., `FlashingYellowState` for night mode) can be added without modifying existing state classes.
- **LSP (Liskov Substitution):** All states conform strictly to `TrafficLightState`.
- **DIP (Dependency Inversion):** Services coordinate via interfaces and domain models rather than concrete state instances.

### Design Patterns
- **State Pattern:** Encapsulates light state behaviors and valid transition rules.
- **Observer / Listener (Applicable for scaling):** Sensor counts trigger timing adjustments.

---

## 8. Main Flows

### Flow 1: Normal Phase Advance
```
IntersectionService.advancePhase(intersectionId, EAST)
  -> Finds active GREEN signal (NORTH)
  -> NORTH.turnYellow() -> [NORTH: YELLOW]
  -> NORTH.turnRed()    -> [NORTH: RED]
  -> EAST.turnGreen()   -> [EAST: GREEN] (Active for configured greenDuration)
```

### Flow 2: Emergency Vehicle Priority Override
```
EmergencyService.requestEmergency(intersectionId, WEST, 15s)
  -> IntersectionCycle.pause() (Saves active phase checkpoint)
  -> Intersection.setEmergencyMode(true, WEST)
  -> Intersection.setAllSignalsToRed()
  -> WEST.turnGreen() -> [WEST: GREEN granted for 15s]
... (Ambulance passes) ...
EmergencyService.endEmergency(intersectionId)
  -> WEST.emergencyTransitionToRed() -> [WEST: RED]
  -> Intersection.setEmergencyMode(false, null)
  -> IntersectionCycle.resume()
  -> Resumes normal cycle from saved phase!
```

---

## 9. Edge Cases

1. **Emergency Request during Active Green:** The currently green signal transitions through `YELLOW` to `RED` before emergency green is granted.
2. **Invalid State Transition:** Attempting to force `RED` to `YELLOW` throws `InvalidStateTransitionException` preventing road accidents.
3. **Cycle Resume Accuracy:** The system remembers exactly which phase was interrupted and resumes without restarting from phase 0.
4. **Traffic Congestion Spikes:** `TimingService` bounds dynamic green durations between safety limits (5s minimum, 120s maximum).

---

## 10. How the Code Works

1. `IntersectionService.createIntersection(101, "Silk Board Junction")` initializes 4 lights in `RED` state.
2. `advancePhase(101, NORTH)` transitions `NORTH` from `RED` $\rightarrow$ `GREEN`.
3. `emergencyService.requestEmergency(101, WEST, 15)` safely turns non-emergency lights to `RED` and activates `WEST` `GREEN`.
4. `emergencyService.endEmergency(101)` clears the emergency and resumes the normal phase cycle.
5. `trafficService.updateVehicleCount()` and `timingService.adjustTimingBasedOnTraffic()` adapt signal timings dynamically based on road load.

---

## 11. How to Run

Compile and execute the self-contained simulation from the `05-Traffic-Signal-System-Design` directory:

```bash
# Compile all Java files
javac -d bin src/*.java

# Run the simulation driver
java -cp bin Main
```

---

## 12. Interview Thinking

### How I Would Explain This in an Interview
1. **Step 1 (Clarify Requirements):** Focus on single intersection (4 lights), standard phase cycle, emergency override, and state transition safety.
2. **Step 2 (Identify Entities):** `TrafficLight`, `Intersection`, `SignalTiming`, `VehicleCounter`, `IntersectionCycle`.
3. **Step 3 (Select Patterns):** Propose the **State Pattern** for traffic lights because signal transitions are safety-critical and follow strict state machine rules.
4. **Step 4 (Emergency Handling):** Walk through the Pause $\rightarrow$ All-Red $\rightarrow$ Priority Green $\rightarrow$ Resume sequence.

### Likely Interviewer Follow-up Questions
1. *How would you coordinate multiple networked intersections (Green Wave)?*
   - **Answer:** Introduce a `CorridorCoordinator` service that offsets signal start times based on average vehicle speed between consecutive intersections.
2. *How do you handle pedestrian crossing requests?*
   - **Answer:** Add a `PedestrianSignal` entity and insert a pedestrian walk phase into `IntersectionCycle` when crossing buttons are pressed.

### Trade-offs
- **State Pattern vs Simple Enum:** An enum with `switch` statements is quicker to write, but the State Pattern guarantees transition safety, prevents invalid state jumps at compile/runtime, and is open for extensions (e.g. night-time flashing mode).

---

## 🎯 Quick Summary

- **Problem:** Design a 4-way traffic signal system with safety-enforced state transitions, emergency priority overrides, and dynamic timing.
- **Core Classes:** `Intersection`, `TrafficLight`, `TrafficLightState` (`RedState`, `GreenState`, `YellowState`), `IntersectionCycle`, `EmergencyService`.
- **Main Flow:** `advancePhase()` $\rightarrow$ Active `GREEN` $\rightarrow$ `YELLOW` $\rightarrow$ `RED` $\rightarrow$ Next Direction `GREEN`.
- **Important Design:** State Pattern for transition validation; Cycle Pause/Resume for emergency handling.
- **Edge Cases:** Invalid transition traps (`InvalidStateTransitionException`), emergency override during phase changes, and bounded dynamic timing (5s–120s).
- **LLD Takeaway:** Never manage safety-critical state machines with raw strings or loose flags; enforce invariants using the State Pattern.
- **Memorable Rule:** *"Transition through Yellow, lock down crossing greens, and pause/resume for emergencies."*
