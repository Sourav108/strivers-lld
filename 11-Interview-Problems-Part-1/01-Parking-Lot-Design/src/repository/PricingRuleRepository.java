package repository;

import domain.PricingRule;
import domain.Vehicle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PricingRuleRepository {
    private final Map<UUID, PricingRule> rules = new ConcurrentHashMap<>();
    private final Map<Vehicle.VehicleType, UUID> vehicleTypeToRuleId = new ConcurrentHashMap<>();

    public PricingRule save(PricingRule rule) {
        rules.put(rule.getId(), rule);
        vehicleTypeToRuleId.put(rule.getVehicleType(), rule.getId());
        return rule;
    }

    public Optional<PricingRule> findById(UUID ruleId) {
        return Optional.ofNullable(rules.get(ruleId));
    }

    public Optional<PricingRule> findByVehicleType(Vehicle.VehicleType vehicleType) {
        UUID ruleId = vehicleTypeToRuleId.get(vehicleType);
        if (ruleId == null) return Optional.empty();
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<PricingRule> findAll() {
        return new ArrayList<>(rules.values());
    }

    public void update(PricingRule rule) {
        rules.put(rule.getId(), rule);
    }
}
