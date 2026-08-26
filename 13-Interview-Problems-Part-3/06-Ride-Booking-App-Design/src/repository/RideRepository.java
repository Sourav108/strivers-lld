package repository;

import domain.Ride;
import domain.RideStatus;

import java.util.List;
import java.util.Optional;

public interface RideRepository {
    Optional<Ride> findByRideId(String rideId);
    Ride save(Ride ride);
    List<Ride> findByRiderId(int riderId);
    List<Ride> findByDriverId(int driverId);
    List<Ride> findByStatus(RideStatus status);
}
