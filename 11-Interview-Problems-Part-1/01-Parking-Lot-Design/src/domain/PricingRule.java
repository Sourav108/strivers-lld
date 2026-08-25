package domain;

import java.util.UUID;

public class PricingRule {
    private final UUID id;
    private final Vehicle.VehicleType vehicleType;
    private double ratePerHour;
    private double flatRate;

    public PricingRule(Vehicle.VehicleType vehicleType, double ratePerHour, double flatRate) {
        this.id = UUID.randomUUID();
        this.vehicleType = vehicleType;
        this.ratePerHour = ratePerHour;
        this.flatRate = flatRate;
    }

    public void updateRates(double ratePerHour, double flatRate) {
        this.ratePerHour = ratePerHour;
        this.flatRate = flatRate;
    }

    public void updateFlatRate(double flatRate) { this.flatRate = flatRate; }
    public void updateHourlyRate(double ratePerHour) { this.ratePerHour = ratePerHour; }

    public UUID getId() { return id; }
    public Vehicle.VehicleType getVehicleType() { return vehicleType; }
    public double getRatePerHour() { return ratePerHour; }
    public double getFlatRate() { return flatRate; }

    @Override
    public String toString() {
        return "PricingRule{" + "type=" + vehicleType + ", ratePerHour=" + ratePerHour + ", flatRate=" + flatRate + '}';
    }
}
