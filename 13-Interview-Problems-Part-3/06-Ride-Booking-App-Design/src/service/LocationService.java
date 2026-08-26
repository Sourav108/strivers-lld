package service;

import domain.Location;
import repository.LocationRepository;

public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void updateDriverLocation(int driverId, Location location) {
        locationRepository.saveLocation(driverId, location);
    }

    public Location getDriverLocation(int driverId) {
        return locationRepository.getLatestLocation(driverId).orElse(null);
    }

    public double calculateDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return 0.0;
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c; // KM
    }

    public long calculateETA(Location from, Location to) {
        double distanceKm = calculateDistance(from, to);
        // Assuming average urban speed of 30 km/h -> 2 minutes per KM
        return Math.round(distanceKm * 120); // seconds
    }
}
