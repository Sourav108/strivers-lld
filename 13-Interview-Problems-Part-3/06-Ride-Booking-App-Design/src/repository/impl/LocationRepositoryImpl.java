package repository.impl;

import domain.Location;
import repository.LocationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LocationRepositoryImpl implements LocationRepository {
    private final Map<Integer, Location> driverLocations = new ConcurrentHashMap<>();

    @Override
    public void saveLocation(int driverId, Location location) {
        driverLocations.put(driverId, location);
    }

    @Override
    public Optional<Location> getLatestLocation(int driverId) {
        return Optional.ofNullable(driverLocations.get(driverId));
    }
}
