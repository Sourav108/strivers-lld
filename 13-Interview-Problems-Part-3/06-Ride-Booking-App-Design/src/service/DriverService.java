package service;

import domain.Driver;
import domain.Location;
import repository.DriverRepository;

import java.util.List;

public class DriverService {
    private final DriverRepository driverRepository;
    private final LocationService locationService;

    public DriverService(DriverRepository driverRepository, LocationService locationService) {
        this.driverRepository = driverRepository;
        this.locationService = locationService;
    }

    public void goOnline(int driverId) {
        driverRepository.findById(driverId).ifPresent(d -> {
            d.setOnline(true);
            System.out.println("🟢 Driver " + d.getName() + " is now ONLINE.");
        });
    }

    public void goOffline(int driverId) {
        driverRepository.findById(driverId).ifPresent(d -> {
            d.setOnline(false);
            System.out.println("🔴 Driver " + d.getName() + " is now OFFLINE.");
        });
    }

    public void updateLocation(int driverId, Location location) {
        driverRepository.updateLocation(driverId, location);
        locationService.updateDriverLocation(driverId, location);
    }

    public Driver getById(int driverId) {
        return driverRepository.findById(driverId).orElse(null);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}
