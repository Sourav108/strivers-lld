package domain.strategy;

public class PricingContext {
    private final double surgeMultiplier;
    private final boolean isNightTime;

    public PricingContext(double surgeMultiplier, boolean isNightTime) {
        this.surgeMultiplier = Math.max(1.0, surgeMultiplier);
        this.isNightTime = isNightTime;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public boolean isNightTime() {
        return isNightTime;
    }
}
