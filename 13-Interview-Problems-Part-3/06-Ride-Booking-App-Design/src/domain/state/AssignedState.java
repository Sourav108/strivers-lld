package domain.state;

import domain.Ride;
import domain.RideStatus;

import java.time.LocalDateTime;

public class AssignedState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already assigned to a driver.");
    }

    @Override
    public void accept(Ride ride, int driverId) {
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("Driver ID " + driverId + " does not match assigned driver.");
        }
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());
    }

    @Override
    public void start(Ride ride, int driverId) {
        throw new IllegalStateException("Ride must be ACCEPTED before starting.");
    }

    @Override
    public void complete(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot complete an assigned ride.");
    }

    @Override
    public void cancel(Ride ride, String reason) {
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        ride.setCancelledAt(LocalDateTime.now());
    }
}
