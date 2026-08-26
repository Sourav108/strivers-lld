package repository.impl;

import domain.Ride;
import domain.RideStatus;
import repository.RideRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RideRepositoryImpl implements RideRepository {
    private final Map<String, Ride> rides = new ConcurrentHashMap<>();

    @Override
    public Optional<Ride> findByRideId(String rideId) {
        return Optional.ofNullable(rides.get(rideId));
    }

    @Override
    public Ride save(Ride ride) {
        rides.put(ride.getRideId(), ride);
        return ride;
    }

    @Override
    public List<Ride> findByRiderId(int riderId) {
        return rides.values().stream().filter(r -> r.getRiderId() == riderId).collect(Collectors.toList());
    }

    @Override
    public List<Ride> findByDriverId(int driverId) {
        return rides.values().stream().filter(r -> r.getDriverId() != null && r.getDriverId() == driverId).collect(Collectors.toList());
    }

    @Override
    public List<Ride> findByStatus(RideStatus status) {
        return rides.values().stream().filter(r -> r.getStatus() == status).collect(Collectors.toList());
    }
}
