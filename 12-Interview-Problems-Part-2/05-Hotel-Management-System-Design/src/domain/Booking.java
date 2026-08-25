package domain;

import java.util.ArrayList;
import java.util.List;

public class Booking {
    private final String id;
    private final String userId;
    private final String hotelId;
    private final String roomTypeId;
    private final DateRange dateRange;
    private final List<NightlyPrice> nightlyPrices;
    private final long totalAmountMinor;
    private BookingStatus bookingStatus;
    private TransactionStatus paymentStatus;
    private String allocatedRoomId;
    private Long checkInTimeUtc;
    private Long checkOutTimeUtc;
    private long holdExpiresAt;
    private final long createdAt;

    public Booking(String id, String userId, String hotelId, String roomTypeId,
                   DateRange dateRange, List<NightlyPrice> nightlyPrices,
                   long totalAmountMinor, long holdExpiresAt) {
        this.id = id;
        this.userId = userId;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.dateRange = dateRange;
        this.nightlyPrices = new ArrayList<>(nightlyPrices);
        this.totalAmountMinor = totalAmountMinor;
        this.bookingStatus = BookingStatus.CREATED;
        this.paymentStatus = TransactionStatus.PENDING;
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getHotelId() { return hotelId; }
    public String getRoomTypeId() { return roomTypeId; }
    public DateRange getDateRange() { return dateRange; }
    public List<NightlyPrice> getNightlyPrices() { return nightlyPrices; }
    public long getTotalAmountMinor() { return totalAmountMinor; }
    public double getTotalAmountRupees() { return totalAmountMinor / 100.0; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus status) { this.bookingStatus = status; }
    public TransactionStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(TransactionStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getAllocatedRoomId() { return allocatedRoomId; }
    public void setAllocatedRoomId(String allocatedRoomId) { this.allocatedRoomId = allocatedRoomId; }
    public Long getCheckInTimeUtc() { return checkInTimeUtc; }
    public void setCheckInTimeUtc(Long checkInTimeUtc) { this.checkInTimeUtc = checkInTimeUtc; }
    public Long getCheckOutTimeUtc() { return checkOutTimeUtc; }
    public void setCheckOutTimeUtc(Long checkOutTimeUtc) { this.checkOutTimeUtc = checkOutTimeUtc; }
    public long getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(long holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Booking[" + id + " | User: " + userId + " | Hotel: " + hotelId + " | RoomType: " + roomTypeId +
                " | Stay: " + dateRange + " | Total: ₹" + getTotalAmountRupees() +
                " | Status: " + bookingStatus + " | Payment: " + paymentStatus + "]";
    }
}
