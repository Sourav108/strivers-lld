package domain;

import java.time.LocalDate;

public class Availability {
    private final String hotelId;
    private final String roomTypeId;
    private final LocalDate date;
    private final int totalRooms;
    private final int bookedRooms;
    private final int availableRooms;

    public Availability(String hotelId, String roomTypeId, LocalDate date, int totalRooms, int bookedRooms, int availableRooms) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.totalRooms = totalRooms;
        this.bookedRooms = bookedRooms;
        this.availableRooms = availableRooms;
    }

    public String getHotelId() { return hotelId; }
    public String getRoomTypeId() { return roomTypeId; }
    public LocalDate getDate() { return date; }
    public int getTotalRooms() { return totalRooms; }
    public int getBookedRooms() { return bookedRooms; }
    public int getAvailableRooms() { return availableRooms; }

    @Override
    public String toString() {
        return date + " [Avail: " + availableRooms + "/" + totalRooms + "]";
    }
}
