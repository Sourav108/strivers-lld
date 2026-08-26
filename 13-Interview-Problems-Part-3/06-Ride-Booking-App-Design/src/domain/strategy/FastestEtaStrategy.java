package domain.strategy;

import domain.Driver;
import domain.Location;

import java.util.List;

public class FastestEtaStrategy implements DriverMatchingStrategy {
    private final NearestDriverStrategy delegate = new NearestDriverStrategy();

    @Override
    public List<Driver> findMatchingDrivers(Location pickup, List<Driver> candidates, int maxResults) {
        // ETA is directly proportional to distance for simulation
        return delegate.findMatchingDrivers(pickup, candidates, maxResults);
    }
}
