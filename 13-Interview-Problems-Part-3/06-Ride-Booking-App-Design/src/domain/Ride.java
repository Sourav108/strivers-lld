package domain;

import java.time.LocalDateTime;

public class Ride {
    private final int id;
    private final String rideId;
    private final int riderId;
    private Integer driverId;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private RideStatus status;
    private final long estimatedFare;
    private final double estimatedDistance;
    private Double actualDistance;
    private final long estimatedDuration;
    private Long actualDuration;
    private final LocalDateTime requestedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private final PaymentType paymentType;
    private String paymentId;
    private PaymentStatus paymentStatus;

    public Ride(int id, String rideId, int riderId, Location pickupLocation, Location dropoffLocation,
                long estimatedFare, double estimatedDistance, long estimatedDuration, PaymentType paymentType) {
        this.id = id;
        this.rideId = rideId;
        this.riderId = riderId;
        this.driverId = null;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = RideStatus.REQUESTED;
        this.estimatedFare = estimatedFare;
        this.estimatedDistance = estimatedDistance;
        this.estimatedDuration = estimatedDuration;
        this.requestedAt = LocalDateTime.now();
        this.paymentType = paymentType;
        this.paymentStatus = (paymentType == PaymentType.POST_PAYMENT) ? PaymentStatus.PENDING : PaymentStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public String getRideId() {
        return rideId;
    }

    public int getRiderId() {
        return riderId;
    }

    public synchronized Integer getDriverId() {
        return driverId;
    }

    public synchronized void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public synchronized RideStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(RideStatus status) {
        this.status = status;
    }

    public long getEstimatedFare() {
        return estimatedFare;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public synchronized Double getActualDistance() {
        return actualDistance;
    }

    public synchronized void setActualDistance(Double actualDistance) {
        this.actualDistance = actualDistance;
    }

    public long getEstimatedDuration() {
        return estimatedDuration;
    }

    public synchronized Long getActualDuration() {
        return actualDuration;
    }

    public synchronized void setActualDuration(Long actualDuration) {
        this.actualDuration = actualDuration;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public synchronized LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public synchronized void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public synchronized LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public synchronized void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public synchronized LocalDateTime getStartedAt() {
        return startedAt;
    }

    public synchronized void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public synchronized LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public synchronized void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public synchronized LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public synchronized void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public synchronized String getCancellationReason() {
        return cancellationReason;
    }

    public synchronized void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public synchronized String getPaymentId() {
        return paymentId;
    }

    public synchronized void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public synchronized PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public synchronized void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getFormattedFare() {
        return String.format("$%.2f", estimatedFare / 100.0);
    }

    @Override
    public synchronized String toString() {
        return "Ride[" + rideId + " | Status=" + status + " | Rider=" + riderId +
                " | Driver=" + (driverId != null ? driverId : "UNASSIGNED") +
                " | Fare=" + getFormattedFare() + " | Payment=" + paymentType + " (" + paymentStatus + ")]";
    }
}
