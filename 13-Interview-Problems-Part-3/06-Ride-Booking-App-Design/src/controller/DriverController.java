package controller;

import domain.Location;
import service.DriverService;
import service.RideService;

public class DriverController {
    private final DriverService driverService;
    private final RideService rideService;

    public DriverController(DriverService driverService, RideService rideService) {
        this.driverService = driverService;
        this.rideService = rideService;
    }

    public void goOnline(int driverId) {
        driverService.goOnline(driverId);
    }

    public void goOffline(int driverId) {
        driverService.goOffline(driverId);
    }

    public void updateLocation(int driverId, Location location) {
        driverService.updateLocation(driverId, location);
    }

    public void acceptRide(String rideId, int driverId) {
        rideService.driverAccept(rideId, driverId);
    }

    public void declineRide(String rideId, int driverId) {
        rideService.driverDecline(rideId, driverId);
    }

    public void startRide(String rideId, int driverId) {
        rideService.startRide(rideId, driverId);
    }

    public void completeRide(String rideId, int driverId) {
        rideService.completeRide(rideId, driverId);
    }
}
