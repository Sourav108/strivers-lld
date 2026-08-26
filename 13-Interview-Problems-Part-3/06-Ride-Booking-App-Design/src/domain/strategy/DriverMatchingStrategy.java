package domain.strategy;

import domain.Driver;
import domain.Location;

import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDrivers(Location pickup, List<Driver> candidates, int maxResults);
}
