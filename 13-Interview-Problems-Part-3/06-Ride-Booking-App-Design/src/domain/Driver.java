package domain;

import java.time.LocalDateTime;

public class Driver {
    private final int id;
    private final String username;
    private final String email;
    private final String phoneNumber;
    private final String name;
    private final String licenseNumber;
    private final String vehicleNumber;
    private final String vehicleType;
    private boolean isOnline;
    private Location currentLocation;
    private LocalDateTime lastLocationUpdate;
    private final LocalDateTime createdAt;

    public Driver(int id, String username, String email, String phoneNumber, String name,
                  String licenseNumber, String vehicleNumber, String vehicleType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.isOnline = false;
        this.currentLocation = null;
        this.lastLocationUpdate = null;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public synchronized boolean isOnline() {
        return isOnline;
    }

    public synchronized void setOnline(boolean online) {
        isOnline = online;
    }

    public synchronized Location getCurrentLocation() {
        return currentLocation;
    }

    public synchronized void updateLocation(Location location) {
        this.currentLocation = location;
        this.lastLocationUpdate = LocalDateTime.now();
    }

    public synchronized LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public synchronized String toString() {
        return "Driver[ID=" + id + ", Name=" + name + ", Vehicle=" + vehicleType +
                " (" + vehicleNumber + "), Online=" + isOnline + ", Loc=" + currentLocation + "]";
    }
}
