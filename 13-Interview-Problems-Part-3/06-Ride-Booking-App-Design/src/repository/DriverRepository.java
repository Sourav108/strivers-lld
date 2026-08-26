package repository;

import domain.Driver;
import domain.Location;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Optional<Driver> findById(int id);
    List<Driver> findAll();
    Driver save(Driver driver);
    void updateLocation(int driverId, Location location);
}
