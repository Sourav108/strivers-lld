package domain.state;

import domain.Ride;

public class CompletedState implements RideState {

    @Override
    public void assign(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot perform actions on an already COMPLETED ride.");
    }

    @Override
    public void accept(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot accept a COMPLETED ride.");
    }

    @Override
    public void start(Ride ride, int driverId) {
        throw new IllegalStateException("Cannot start a COMPLETED ride.");
    }

    @Override
    public void complete(Ride ride, int driverId) {
        throw new IllegalStateException("Ride is already COMPLETED.");
    }

    @Override
    public void cancel(Ride ride, String reason) {
        throw new IllegalStateException("Cannot cancel an already COMPLETED ride.");
    }
}
