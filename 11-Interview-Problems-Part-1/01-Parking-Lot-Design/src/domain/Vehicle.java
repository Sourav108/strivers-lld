package domain;

import java.util.UUID;

public class Vehicle {
    public enum VehicleType {
        BIKE,
        CAR,
        TRUCK,
        EV
    }

    private final UUID id;
    private final String licensePlate;
    private final VehicleType vehicleType;

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        this.id = UUID.randomUUID();
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    public UUID getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getVehicleType() { return vehicleType; }

    @Override
    public String toString() {
        return "Vehicle{" + "licensePlate='" + licensePlate + '\'' + ", type=" + vehicleType + '}';
    }
}
