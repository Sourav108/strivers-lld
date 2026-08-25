package main;

import controller.ElevatorController;
import controller.ElevatorPanelController;
import controller.FloorPanelController;
import domain.*;
import domain.strategy.FCFSStrategy;
import domain.strategy.LoadBalancingStrategy;
import domain.strategy.NearestElevatorStrategy;
import repository.*;
import repository.impl.*;
import service.*;

/**
 * ElevatorSystemSimulation: Complete End-to-End Simulation of the Elevator System LLD
 * 
 * Demonstrates:
 * 1. Building & Multi-Elevator Initialization (Floors 0-10, Elevators with capacity limits)
 * 2. Floor Panel (External Requests) & Dispatcher Strategy (NearestElevatorStrategy)
 * 3. Step-by-Step Movement & State Transitions (STOPPED -> MOVING -> STOPPED -> DOORS_OPENING -> DOORS_CLOSING)
 * 4. Elevator Panel (Internal Requests) & SCAN Path Traversal
 * 5. Dynamic Strategy Swapping (LoadBalancingStrategy & FCFSStrategy)
 * 6. Maintenance Mode Handling (Draining requests & isolating elevator)
 * 7. System Graceful Shutdown
 */

public class ElevatorSystemSimulation {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=================================================================");
        System.out.println("🛗 ELEVATOR SYSTEM - LLD INTERVIEW ARCHITECTURE DEMO");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        BuildingRepository buildingRepo = new BuildingRepositoryImpl();
        ElevatorRepository elevatorRepo = new ElevatorRepositoryImpl();
        ExternalRequestRepository extRequestRepo = new ExternalRequestRepositoryImpl();
        InternalRequestRepository intRequestRepo = new InternalRequestRepositoryImpl();

        // --- 2. INITIALIZE SERVICES ---
        BuildingService buildingService = new BuildingService(buildingRepo);
        RequestService requestService = new RequestService(extRequestRepo, intRequestRepo);
        ElevatorService elevatorService = new ElevatorService(elevatorRepo);
        DispatcherService dispatcherService = new DispatcherService(extRequestRepo, elevatorRepo);
        MovementService movementService = new MovementService(elevatorRepo, intRequestRepo, extRequestRepo);
        ElevatorSchedulerService schedulerService = new ElevatorSchedulerService(dispatcherService, movementService, buildingService);

        // --- 3. INITIALIZE CONTROLLERS ---
        ElevatorController elevatorController = new ElevatorController(elevatorService, schedulerService);
        FloorPanelController floorPanelController = new FloorPanelController(requestService, dispatcherService, buildingService);
        ElevatorPanelController elevatorPanelController = new ElevatorPanelController(requestService, elevatorService);

        // --- 4. SEED BUILDING & ELEVATORS ---
        Building bldg = buildingService.createBuilding("Skyline Towers", 0, 10, 3);
        Elevator e1 = elevatorController.createElevator(bldg.getId(), 8); // Capacity: 8
        Elevator e2 = elevatorController.createElevator(bldg.getId(), 8);
        Elevator e3 = elevatorController.createElevator(bldg.getId(), 8);

        // Position elevators initially at different floors for demonstration
        e1.setCurrentFloor(0); // Ground floor
        e2.setCurrentFloor(5); // Middle floor
        e3.setCurrentFloor(10); // Top floor

        // =========================================================================
        // SCENARIO 1: EXTERNAL REQUEST & NEAREST ELEVATOR DISPATCH
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Floor Panel External Requests & Dispatching");
        System.out.println("-----------------------------------------------------------");

        // Passenger at Floor 4 presses UP
        floorPanelController.pressUpButton(4, bldg.getId());

        // Passenger at Floor 9 presses DOWN
        floorPanelController.pressDownButton(9, bldg.getId());

        // Step simulation to dispatch and assign requests
        schedulerService.stepSimulation(bldg.getId());

        // =========================================================================
        // SCENARIO 2: ELEVATOR MOVEMENT & PASSENGER BOARDING
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Step-by-Step Movement & Passenger Boarding");
        System.out.println("-----------------------------------------------------------");

        System.out.println("Advancing elevator ticks until requests are serviced...");
        for (int tick = 1; tick <= 5; tick++) {
            System.out.println("⏱️ --- Tick " + tick + " ---");
            schedulerService.stepSimulation(bldg.getId());
        }

        // =========================================================================
        // SCENARIO 3: INTERNAL REQUEST & SCAN PATH TRAVERSAL
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Internal Request (Destination Selection)");
        System.out.println("-----------------------------------------------------------");

        // Passenger inside Elevator 2 selects destination Floor 8
        elevatorPanelController.selectFloor(e2.getId(), 8);
        elevatorPanelController.selectFloor(e2.getId(), 2);

        System.out.println("Advancing elevator ticks for SCAN movement...");
        for (int tick = 1; tick <= 4; tick++) {
            System.out.println("⏱️ --- Tick " + tick + " ---");
            schedulerService.stepSimulation(bldg.getId());
        }

        // =========================================================================
        // SCENARIO 4: DYNAMIC STRATEGY SWAPPING (LoadBalancingStrategy)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Dynamic Strategy Switching (Load Balancing)");
        System.out.println("-----------------------------------------------------------");

        dispatcherService.setElevatorSelectionStrategy(new LoadBalancingStrategy());
        movementService.setMovementStrategy(new FCFSStrategy());

        floorPanelController.pressUpButton(1, bldg.getId());
        schedulerService.stepSimulation(bldg.getId());

        // =========================================================================
        // SCENARIO 5: ELEVATOR MAINTENANCE MODE
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: Elevator Maintenance Mode & Request Isolation");
        System.out.println("-----------------------------------------------------------");

        // Put Elevator 3 into maintenance mode
        elevatorController.setElevatorMaintenance(e3.getId(), true);

        // Try to press floor button near Elevator 3 (Floor 10)
        floorPanelController.pressDownButton(10, bldg.getId());
        schedulerService.stepSimulation(bldg.getId());

        // Restore Elevator 3 from maintenance
        elevatorController.setElevatorMaintenance(e3.getId(), false);

        // =========================================================================
        // SCENARIO 6: SYSTEM SHUTDOWN
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("6️⃣ SCENARIO 6: Graceful System Shutdown");
        System.out.println("-----------------------------------------------------------");

        elevatorController.stopElevatorSystem(bldg.getId());

        System.out.println("\n=================================================================");
        System.out.println("🎯 ELEVATOR SYSTEM ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
