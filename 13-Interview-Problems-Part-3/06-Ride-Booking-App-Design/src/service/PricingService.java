package service;

import domain.FareEstimateResponse;
import domain.Location;
import domain.strategy.BasePricingStrategy;
import domain.strategy.PricingContext;
import domain.strategy.PricingStrategy;

public class PricingService {
    private PricingStrategy pricingStrategy;
    private final LocationService locationService;

    public PricingService(LocationService locationService) {
        this.locationService = locationService;
        this.pricingStrategy = new BasePricingStrategy();
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public FareEstimateResponse calculateFare(Location pickup, Location dropoff) {
        return calculateFare(pickup, dropoff, new PricingContext(1.0, false));
    }

    public FareEstimateResponse calculateFare(Location pickup, Location dropoff, PricingContext context) {
        double distanceKm = locationService.calculateDistance(pickup, dropoff);
        long durationSeconds = locationService.calculateETA(pickup, dropoff);
        long fareCents = pricingStrategy.calculateFare(distanceKm, durationSeconds, context);
        return new FareEstimateResponse(fareCents, distanceKm, durationSeconds, "USD");
    }
}
