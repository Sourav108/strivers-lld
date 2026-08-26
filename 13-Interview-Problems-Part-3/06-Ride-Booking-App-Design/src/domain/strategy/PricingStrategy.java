package domain.strategy;

public interface PricingStrategy {
    long calculateFare(double distanceKm, long durationSeconds, PricingContext context);
}
