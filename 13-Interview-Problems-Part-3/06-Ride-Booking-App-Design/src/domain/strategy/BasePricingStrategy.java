package domain.strategy;

public class BasePricingStrategy implements PricingStrategy {
    private static final long BASE_FARE_CENTS = 250;      // $2.50 base fare
    private static final long PER_KM_RATE_CENTS = 120;    // $1.20 per km
    private static final long PER_MINUTE_RATE_CENTS = 25; // $0.25 per minute

    @Override
    public long calculateFare(double distanceKm, long durationSeconds, PricingContext context) {
        long distanceCost = Math.round(distanceKm * PER_KM_RATE_CENTS);
        long timeCost = Math.round((durationSeconds / 60.0) * PER_MINUTE_RATE_CENTS);
        return BASE_FARE_CENTS + distanceCost + timeCost;
    }
}
