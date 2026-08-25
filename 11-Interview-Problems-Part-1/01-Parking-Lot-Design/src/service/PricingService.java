package service;

import domain.PricingRule;
import domain.Vehicle;
import repository.PricingRuleRepository;

import java.util.Optional;

public class PricingService {
    private final PricingRuleRepository pricingRuleRepository;

    public PricingService(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    public double calculateFee(Vehicle.VehicleType vehicleType, long durationHours) {
        Optional<PricingRule> ruleOpt = pricingRuleRepository.findByVehicleType(vehicleType);
        if (ruleOpt.isEmpty()) {
            // Default fallback pricing
            return Math.max(1, durationHours) * 20.0;
        }

        PricingRule rule = ruleOpt.get();
        long hours = Math.max(1, durationHours);
        double hourlyTotal = hours * rule.getRatePerHour();
        double flatTotal = rule.getFlatRate();

        // System charges the minimum of flat rate or hourly rate (or flat rate for short stay, hourly for long stay)
        if (flatTotal > 0 && hourlyTotal > flatTotal) {
            return flatTotal;
        }
        return hourlyTotal;
    }
}
