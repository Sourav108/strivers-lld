package domain.state;

import domain.Ride;

/**
 * State Pattern interface defining legal lifecycle actions on a Ride.
 */
public interface RideState {
    void assign(Ride ride, int driverId);
    void accept(Ride ride, int driverId);
    void start(Ride ride, int driverId);
    void complete(Ride ride, int driverId);
    void cancel(Ride ride, String reason);
}
