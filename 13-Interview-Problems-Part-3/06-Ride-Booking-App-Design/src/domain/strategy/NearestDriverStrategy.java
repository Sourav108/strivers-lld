package domain.strategy;

import domain.Driver;
import domain.Location;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public List<Driver> findMatchingDrivers(Location pickup, List<Driver> candidates, int maxResults) {
        return candidates.stream()
                .filter(d -> d.isOnline() && d.getCurrentLocation() != null)
                .sorted(Comparator.comparingDouble(d -> calculateDistance(pickup, d.getCurrentLocation())))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private double calculateDistance(Location loc1, Location loc2) {
        // Haversine formula calculation for geographical distance in KM
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c; // Earth radius ~6371 KM
    }
}
