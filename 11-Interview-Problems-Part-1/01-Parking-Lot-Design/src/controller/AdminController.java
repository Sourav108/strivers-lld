package controller;

import domain.PricingRule;
import domain.Vehicle;
import service.AdminService;

import java.util.Map;

public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public void addFloor(int floorNumber) {
        adminService.addFloor(floorNumber);
    }

    public void addSlotsToFloor(int floorNumber, Vehicle.VehicleType slotType, int count) {
        adminService.addSlotsToFloor(floorNumber, slotType, count);
    }

    public void updatePricingRule(Vehicle.VehicleType vehicleType, double ratePerHour, double flatRate) {
        adminService.updatePricingRule(vehicleType, ratePerHour, flatRate);
    }

    public void addPricingRule(PricingRule rule) {
        adminService.addPricingRule(rule);
    }

    public Map<String, Object> getParkingStatus() {
        return adminService.getParkingStatus();
    }
}
