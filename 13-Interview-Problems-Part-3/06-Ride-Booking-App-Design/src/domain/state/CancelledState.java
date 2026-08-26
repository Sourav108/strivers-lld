package domain.state;

import domain.Ride;

public class CancelledState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot assign a CANCELLED ride.");
    }

    @Override
    public void accept(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot accept a CANCELLED ride.");
    }

    @Override
    public void start(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot start a CANCELLED ride.");
    }

    @Override
    public void complete(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot complete a CANCELLED ride.");
    }

    @Override
    public void cancel(Ride ride, String reason) {
        throw new IllegalStateException("Ride is already CANCELLED.");
    }
}
