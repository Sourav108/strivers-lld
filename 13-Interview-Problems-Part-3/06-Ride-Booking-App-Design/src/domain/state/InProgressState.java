package domain.state;

import domain.Ride;
import domain.RideStatus;

import java.time.LocalDateTime;

public class InProgressState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot reassign an ongoing ride.");
    }

    @Override
    public void accept(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already in progress.");
    }

    @Override
    public void start(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already started.");
    }

    @Override
    public void complete(Ride ride, int driverId) {
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("Driver ID " + driverId + " does not match ride driver.");
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
    }

    @Override
    public void cancel(Ride ride, String reason) {
        // In-progress ride cancellation by driver/rider
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        ride.setCancelledAt(LocalDateTime.now());
    }
}
