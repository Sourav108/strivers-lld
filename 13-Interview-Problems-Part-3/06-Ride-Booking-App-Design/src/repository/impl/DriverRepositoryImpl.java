package repository.impl;

import domain.Driver;
import domain.Location;
import repository.DriverRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DriverRepositoryImpl implements DriverRepository {
    private final Map<Integer, Driver> drivers = new ConcurrentHashMap<>();

    @Override
    public Optional<Driver> findById(int id) {
        return Optional.ofNullable(drivers.get(id));
    }

    @Override
    public List<Driver> findAll() {
        return new ArrayList<>(drivers.values());
    }

    @Override
    public Driver save(Driver driver) {
        drivers.put(driver.getId(), driver);
        return driver;
    }

    @Override
    public void updateLocation(int driverId, Location location) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.updateLocation(location);
        }
    }
}
