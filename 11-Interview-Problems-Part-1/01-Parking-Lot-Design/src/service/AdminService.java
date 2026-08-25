package service;

import domain.Floor;
import domain.ParkingSlot;
import domain.PricingRule;
import domain.Vehicle;
import repository.FloorRepository;
import repository.PricingRuleRepository;
import repository.SlotRepository;

import java.util.*;

public class AdminService {
    private final FloorRepository floorRepository;
    private final SlotRepository slotRepository;
    private final PricingRuleRepository pricingRuleRepository;

    public AdminService(FloorRepository floorRepository, SlotRepository slotRepository, PricingRuleRepository pricingRuleRepository) {
        this.floorRepository = floorRepository;
        this.slotRepository = slotRepository;
        this.pricingRuleRepository = pricingRuleRepository;
    }

    public void addFloor(int floorNumber) {
        Floor floor = new Floor(floorNumber);
        floorRepository.save(floor);
        System.out.println("🏢 [Admin] Added Floor #" + floorNumber);
    }

    public void addSlotsToFloor(int floorNumber, Vehicle.VehicleType slotType, int count) {
        Optional<Floor> floorOpt = floorRepository.findByNumber(floorNumber);
        if (floorOpt.isEmpty()) {
            throw new IllegalArgumentException("Floor #" + floorNumber + " does not exist.");
        }

        Floor floor = floorOpt.get();
        for (int i = 0; i < count; i++) {
            ParkingSlot slot = new ParkingSlot(slotType, floorNumber);
            floor.addSlot(slot);
            slotRepository.save(slot);
        }
        System.out.println("🅿️ [Admin] Added " + count + " " + slotType + " slots to Floor #" + floorNumber);
    }

    public void addPricingRule(PricingRule rule) {
        pricingRuleRepository.save(rule);
        System.out.println("💲 [Admin] Configured Pricing Rule for " + rule.getVehicleType() + ": ₹" + rule.getRatePerHour() + "/hr (Flat: ₹" + rule.getFlatRate() + ")");
    }

    public void updatePricingRule(Vehicle.VehicleType vehicleType, double ratePerHour, double flatRate) {
        Optional<PricingRule> ruleOpt = pricingRuleRepository.findByVehicleType(vehicleType);
        if (ruleOpt.isPresent()) {
            PricingRule rule = ruleOpt.get();
            rule.updateRates(ratePerHour, flatRate);
            pricingRuleRepository.update(rule);
        } else {
            addPricingRule(new PricingRule(vehicleType, ratePerHour, flatRate));
        }
    }

    public Map<String, Object> getParkingStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalFloors", floorRepository.findAll().size());
        status.put("totalSlots", slotRepository.getAllSlots().size());
        status.put("availableSlotsByType", slotRepository.getSlotStatistics());
        return status;
    }
}
