package domain;

import java.time.LocalDateTime;

public class RideStatusResponse {
    private final String rideId;
    private final RideStatus status;
    private final Integer driverId;
    private final String driverName;
    private final String vehicleNumber;
    private final Location driverLocation;
    private final Long etaSeconds;
    private final long estimatedFare;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private final LocalDateTime requestedAt;

    public RideStatusResponse(String rideId, RideStatus status, Integer driverId, String driverName,
                              String vehicleNumber, Location driverLocation, Long etaSeconds,
                              long estimatedFare, Location pickupLocation, Location dropoffLocation,
                              LocalDateTime requestedAt) {
        this.rideId = rideId;
        this.status = status;
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
        this.driverLocation = driverLocation;
        this.etaSeconds = etaSeconds;
        this.estimatedFare = estimatedFare;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.requestedAt = requestedAt;
    }

    public String getRideId() {
        return rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Location getDriverLocation() {
        return driverLocation;
    }

    public Long getEtaSeconds() {
        return etaSeconds;
    }

    public long getEstimatedFare() {
        return estimatedFare;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    @Override
    public String toString() {
        return "RideStatusResponse[" + rideId + " | Status=" + status +
                (driverName != null ? " | Driver=" + driverName + " (" + vehicleNumber + ")" : " | No Driver") +
                (etaSeconds != null ? " | ETA=" + (etaSeconds / 60) + " mins" : "") + "]";
    }
}
