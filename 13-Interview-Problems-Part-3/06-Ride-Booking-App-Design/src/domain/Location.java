package domain;

import java.time.LocalDateTime;

/**
 * Represents a geographical coordinate with latitude, longitude, and optional address.
 */
public class Location {
    private final double latitude;
    private final double longitude;
    private final String address;
    private final LocalDateTime timestamp;

    public Location(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.timestamp = LocalDateTime.now();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f%s)", latitude, longitude,
                address != null ? " - " + address : "");
    }
}
