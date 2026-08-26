package service;

import domain.Driver;
import domain.NotificationMessage;
import domain.Ride;
import domain.strategy.DriverMatchingStrategy;
import domain.strategy.NearestDriverStrategy;
import service.notification.NotificationRouter;

import java.util.List;
import java.util.Optional;

public class MatchingService {
    private final DriverService driverService;
    private final LockService lockService;
    private final NotificationRouter notificationRouter;
    private DriverMatchingStrategy matchingStrategy;

    public MatchingService(DriverService driverService, LockService lockService, NotificationRouter notificationRouter) {
        this.driverService = driverService;
        this.lockService = lockService;
        this.notificationRouter = notificationRouter;
        this.matchingStrategy = new NearestDriverStrategy();
    }

    public void setMatchingStrategy(DriverMatchingStrategy matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }

    public Optional<Driver> matchDriver(Ride ride) {
        List<Driver> allDrivers = driverService.getAllDrivers();
        List<Driver> candidates = matchingStrategy.findMatchingDrivers(ride.getPickupLocation(), allDrivers, 3);

        for (Driver driver : candidates) {
            String lockKey = "driver_lock_" + driver.getId();
            boolean acquired = lockService.acquire(lockKey, 200);
            if (!acquired) {
                continue; // Skip busy or locked driver
            }
            try {
                if (driver.isOnline()) {
                    notificationRouter.send("SMS", new NotificationMessage(
                            driver.getPhoneNumber(),
                            "New Ride Offer",
                            "Ride " + ride.getRideId() + " available near " + ride.getPickupLocation() + ". Fare: " + ride.getFormattedFare()
                    ));
                    return Optional.of(driver);
                }
            } finally {
                lockService.release(lockKey);
            }
        }
        return Optional.empty();
    }
}
