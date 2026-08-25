package domain;

import java.util.List;

public class RoomTypeAvailability {
    private final String roomTypeId;
    private final String roomTypeName;
    private final int capacity;
    private final String bedType;
    private final List<String> amenities;
    private final boolean available;
    private final long totalPriceMinor;
    private final double averagePricePerNight;
    private final List<NightlyPrice> nightlyPrices;

    public RoomTypeAvailability(String roomTypeId, String roomTypeName, int capacity, String bedType,
                                List<String> amenities, boolean available, long totalPriceMinor,
                                double averagePricePerNight, List<NightlyPrice> nightlyPrices) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.capacity = capacity;
        this.bedType = bedType;
        this.amenities = amenities;
        this.available = available;
        this.totalPriceMinor = totalPriceMinor;
        this.averagePricePerNight = averagePricePerNight;
        this.nightlyPrices = nightlyPrices;
    }

    public String getRoomTypeId() { return roomTypeId; }
    public String getRoomTypeName() { return roomTypeName; }
    public int getCapacity() { return capacity; }
    public String getBedType() { return bedType; }
    public List<String> getAmenities() { return amenities; }
    public boolean isAvailable() { return available; }
    public long getTotalPriceMinor() { return totalPriceMinor; }
    public double getTotalPriceRupees() { return totalPriceMinor / 100.0; }
    public double getAveragePricePerNight() { return averagePricePerNight; }
    public List<NightlyPrice> getNightlyPrices() { return nightlyPrices; }

    @Override
    public String toString() {
        return "RoomTypeAvailability[" + roomTypeName + " (" + roomTypeId + ") | Avail: " + available +
                " | Total: ₹" + getTotalPriceRupees() + " (Avg/Night: ₹" + (averagePricePerNight / 100.0) + ")]";
    }
}
