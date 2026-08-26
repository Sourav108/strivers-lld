package domain;

public class RideRequest {
    private final int riderId;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private final PaymentType paymentType;

    public RideRequest(int riderId, Location pickupLocation, Location dropoffLocation, PaymentType paymentType) {
        this.riderId = riderId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.paymentType = paymentType;
    }

    public int getRiderId() {
        return riderId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}
