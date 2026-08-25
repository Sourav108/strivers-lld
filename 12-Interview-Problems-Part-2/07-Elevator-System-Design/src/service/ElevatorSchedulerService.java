package service;

import domain.SystemState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ElevatorSchedulerService {
    private final DispatcherService dispatcherService;
    private final MovementService movementService;
    private final BuildingService buildingService;
    private ScheduledExecutorService scheduler;

    public ElevatorSchedulerService(DispatcherService dispatcherService,
                                    MovementService movementService,
                                    BuildingService buildingService) {
        this.dispatcherService = dispatcherService;
        this.movementService = movementService;
        this.buildingService = buildingService;
    }

    public synchronized void startScheduler(String buildingId) {
        if (scheduler != null && !scheduler.isShutdown()) return;

        buildingService.setBuildingSystemState(buildingId, SystemState.RUNNING);
        scheduler = Executors.newScheduledThreadPool(2);

        // 1-second interval for dispatching requests
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (buildingService.isSystemRunning(buildingId)) {
                    dispatcherService.processPendingRequests(buildingId);
                }
            } catch (Exception e) {
                System.err.println("Error in Dispatcher Scheduler: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);

        // 2-second interval for elevator movement simulation
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (buildingService.isSystemRunning(buildingId)) {
                    movementService.processAllElevatorMovements(buildingId);
                }
            } catch (Exception e) {
                System.err.println("Error in Movement Scheduler: " + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);

        System.out.println("⚡ [Elevator Scheduler Active] Dispatching every 1s, Movement every 2s.");
    }

    public synchronized void stopScheduler(String buildingId) {
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPING);
        System.out.println("⏳ [Elevator Scheduler Stopping] Draining pending requests...");

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPED);
        System.out.println("🛑 [Elevator Scheduler Stopped] System is now STOPPED.");
    }

    public void stepSimulation(String buildingId) {
        dispatcherService.processPendingRequests(buildingId);
        movementService.processAllElevatorMovements(buildingId);
    }
}
