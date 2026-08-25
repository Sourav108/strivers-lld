package domain;

public class Room {
    private final String id;
    private final String hotelId;
    private final String roomTypeId;
    private final String roomNumber;
    private boolean isActive;

    public Room(String id, String hotelId, String roomTypeId, String roomNumber) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.roomNumber = roomNumber;
        this.isActive = true;
    }

    public String getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getRoomTypeId() { return roomTypeId; }
    public String getRoomNumber() { return roomNumber; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Room[" + roomNumber + " (Type: " + roomTypeId + ")]";
    }
}
