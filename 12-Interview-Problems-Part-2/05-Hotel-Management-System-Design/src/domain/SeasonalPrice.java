package domain;

import java.time.LocalDate;

public class SeasonalPrice {
    private final String id;
    private final String hotelId;
    private final String roomTypeId;
    private final LocalDate date;
    private final long priceMinor;

    public SeasonalPrice(String id, String hotelId, String roomTypeId, LocalDate date, long priceMinor) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.priceMinor = priceMinor;
    }

    public String getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getRoomTypeId() { return roomTypeId; }
    public LocalDate getDate() { return date; }
    public long getPriceMinor() { return priceMinor; }
    public double getPriceRupees() { return priceMinor / 100.0; }

    @Override
    public String toString() {
        return "SeasonalPrice[" + hotelId + " | " + roomTypeId + " | " + date + " -> ₹" + (priceMinor / 100.0) + "]";
    }
}
