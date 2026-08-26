package domain.strategy;

public class SurgePricingStrategy implements PricingStrategy {
    private final BasePricingStrategy basePricing = new BasePricingStrategy();

    @Override
    public long calculateFare(double distanceKm, long durationSeconds, PricingContext context) {
        long base = basePricing.calculateFare(distanceKm, durationSeconds, context);
        double multiplier = (context != null) ? context.getSurgeMultiplier() : 1.0;
        return Math.round(base * multiplier);
    }
}
