package domain.state;

import domain.Ride;
import domain.RideStatus;

import java.time.LocalDateTime;

public class AcceptedState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already accepted.");
    }

    @Override
    public void accept(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already accepted.");
    }

    @Override
    public void start(Ride ride, int driverId) {
        if (ride.getDriverId() == null || ride.getDriverId() != driverId) {
            throw new IllegalArgumentException("Driver ID " + driverId + " does not match accepted driver.");
        }
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());
    }

    @Override
    public void complete(Ride ride, int driverId) {
        throw new IllegalStateException("Ride must be IN_PROGRESS before completing.");
    }

    @Override
    public void cancel(Ride ride, String reason) {
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        ride.setCancelledAt(LocalDateTime.now());
    }
}
