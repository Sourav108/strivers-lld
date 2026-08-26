package elevator;

/**
 * Driver simulation for the Elevator System LLD.
 * Demonstrates:
 * 1. Building initialization with multiple elevators and floors
 * 2. External hall calls and nearest elevator dispatching
 * 3. Internal cabin calls and LOOK/SCAN scheduling
 * 4. Step-by-step elevator movement, door safety, and passenger boarding/alighting
 * 5. Capacity limit enforcement
 * 6. Maintenance mode & emergency overrides
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🏢 SMART ELEVATOR SYSTEM - LOW LEVEL DESIGN DEMO");
        System.out.println("==================================================");

        // 1. Initialize Elevator System: Floors 0 to 10, 3 Elevators, Capacity = 5 passengers
        int minFloor = 0;
        int maxFloor = 10;
        int numElevators = 3;
        int elevatorCapacity = 5;

        ElevatorSystem system = new ElevatorSystem(minFloor, maxFloor, numElevators, elevatorCapacity);
        FloorPanel floor3Panel = new FloorPanel(3, system);
        FloorPanel floor7Panel = new FloorPanel(7, system);

        system.displayStatus();

        // 2. Scenario 1: External Hall Requests
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 SCENARIO 1: External Hall Requests");
        System.out.println("--------------------------------------------------");
        // Passenger on Floor 3 wants to go UP
        floor3Panel.pressUpButton();
        // Passenger on Floor 7 wants to go DOWN
        floor7Panel.pressDownButton();

        system.displayStatus();

        // 3. Step through movement until elevators reach their targets
        System.out.println("\n--------------------------------------------------");
        System.out.println("🚀 SIMULATING ELEVATOR MOVEMENT (LOOK/SCAN)");
        System.out.println("--------------------------------------------------");
        system.runUntilIdle(10);

        system.displayStatus();

        // 4. Scenario 2: Passenger Boarding & Internal Destination Selection
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 SCENARIO 2: Passenger Boarding & Internal Cabin Call");
        System.out.println("--------------------------------------------------");
        Elevator elevator1 = system.getElevator(1);
        ElevatorPanel elevator1Panel = new ElevatorPanel(1, system);

        // Board 3 passengers onto Elevator 1 at Floor 3
        elevator1.boardPassengers(3);
        // Passengers select destination Floor 8
        elevator1Panel.pressFloorButton(8);

        // Advance simulation
        system.runUntilIdle(10);

        // Passengers exit at destination Floor 8
        elevator1.exitPassengers(3);
        system.displayStatus();

        // 5. Scenario 3: Capacity Overflow Edge Case
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 SCENARIO 3: Capacity Limit Handling");
        System.out.println("--------------------------------------------------");
        Elevator elevator2 = system.getElevator(2);
        System.out.println("Attempting to board 4 passengers (Capacity: 5)...");
        elevator2.boardPassengers(4);
        System.out.println("Attempting to board 3 MORE passengers (Total 7 > 5)...");
        boolean boarded = elevator2.boardPassengers(3); // Should fail!
        System.out.println("Boarding success: " + boarded + " (Capacity constraint respected!)");

        // 6. Scenario 4: Maintenance Mode & Emergency Override
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 SCENARIO 4: Maintenance Mode");
        System.out.println("--------------------------------------------------");
        ElevatorPanel elevator2Panel = new ElevatorPanel(2, system);
        elevator2Panel.pressEmergencyStop(); // Takes Elevator 2 offline

        // External request at Floor 5 UP — System should bypass Elevator 2 and dispatch Elevator 1 or 3
        FloorPanel floor5Panel = new FloorPanel(5, system);
        floor5Panel.pressUpButton();

        system.runUntilIdle(10);
        system.displayStatus();

        System.out.println("\n==================================================");
        System.out.println("✅ ELEVATOR SYSTEM SIMULATION COMPLETED SUCCESSFULLY");
        System.out.println("==================================================");
    }
}
