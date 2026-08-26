package domain;

public class FareEstimateResponse {
    private final long estimatedFare;
    private final double estimatedDistance;
    private final long estimatedDuration;
    private final String currency;

    public FareEstimateResponse(long estimatedFare, double estimatedDistance, long estimatedDuration, String currency) {
        this.estimatedFare = estimatedFare;
        this.estimatedDistance = estimatedDistance;
        this.estimatedDuration = estimatedDuration;
        this.currency = currency;
    }

    public long getEstimatedFare() {
        return estimatedFare;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public long getEstimatedDuration() {
        return estimatedDuration;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFormattedFare() {
        return String.format("$%.2f %s", estimatedFare / 100.0, currency);
    }

    @Override
    public String toString() {
        return String.format("FareEstimate[Fare=%s, Distance=%.2f km, Duration=%d mins]",
                getFormattedFare(), estimatedDistance, estimatedDuration / 60);
    }
}
