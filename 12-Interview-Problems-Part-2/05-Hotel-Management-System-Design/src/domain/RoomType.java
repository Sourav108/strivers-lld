package domain;

import java.util.ArrayList;
import java.util.List;

public class RoomType {
    private final String id;
    private final String hotelId;
    private final String name;
    private final int capacity;
    private final String bedType;
    private final long basePriceMinor;
    private final List<String> amenities;
    private int totalRooms;
    private boolean isActive;

    public RoomType(String id, String hotelId, String name, int capacity, String bedType, long basePriceMinor, int totalRooms) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.capacity = capacity;
        this.bedType = bedType;
        this.basePriceMinor = basePriceMinor;
        this.totalRooms = totalRooms;
        this.amenities = new ArrayList<>();
        this.isActive = true;
    }

    public String getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getBedType() { return bedType; }
    public long getBasePriceMinor() { return basePriceMinor; }
    public double getBasePriceRupees() { return basePriceMinor / 100.0; }
    public List<String> getAmenities() { return amenities; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public boolean isActive() { return isActive; }
    public void addAmenity(String amenity) { this.amenities.add(amenity); }

    @Override
    public String toString() {
        return "RoomType[" + id + " | " + name + " | Cap: " + capacity + " | ₹" + (basePriceMinor / 100.0) + "/night | Rooms: " + totalRooms + "]";
    }
}
