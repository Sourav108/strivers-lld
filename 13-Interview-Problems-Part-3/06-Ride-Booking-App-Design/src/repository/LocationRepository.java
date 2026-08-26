package repository;

import domain.Location;

import java.util.Optional;

public interface LocationRepository {
    void saveLocation(int driverId, Location location);
    Optional<Location> getLatestLocation(int driverId);
}
