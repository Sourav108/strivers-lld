package domain.state;

import domain.Ride;
import domain.RideStatus;

import java.time.LocalDateTime;

public class RequestedState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(LocalDateTime.now());
    }

    @Override
    public void accept(Ride ride, int driverId) {
        throw new IllegalStateException("Ride must be ASSIGNED to a driver before being ACCEPTED.");
    }

    @Override
    public void start(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot start a ride in REQUESTED state.");
    }

    @Override
    public void complete(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot complete a ride in REQUESTED state.");
    }

    @Override
    public void cancel(Ride ride, String reason) {
        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        ride.setCancelledAt(LocalDateTime.now());
    }
}
