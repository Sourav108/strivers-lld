package service;

import domain.DateRange;
import domain.Hotel;
import domain.RoomType;
import repository.BookingRepository;
import repository.HotelRepository;
import repository.RoomTypeRepository;

import java.time.LocalDate;

public class InventoryService {
    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    public InventoryService(BookingRepository bookingRepository, RoomTypeRepository roomTypeRepository, HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
    }

    public int getAvailableRoomsOnDate(String hotelId, String roomTypeId, LocalDate date) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel #" + hotelId + " not found."));

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("RoomType #" + roomTypeId + " not found."));

        int totalRooms = roomType.getTotalRooms();
        int overbookAllowed = (int) Math.ceil(totalRooms * hotel.getDefaultOverbookPercent() / 100.0);
        int maxCapacity = totalRooms + overbookAllowed;

        int activeBookings = bookingRepository.countActiveBookingsOnDate(hotelId, roomTypeId, date);
        return Math.max(0, maxCapacity - activeBookings);
    }

    public boolean checkAvailability(String hotelId, String roomTypeId, DateRange range, int qty) {
        for (LocalDate date : range.getDates()) {
            if (getAvailableRoomsOnDate(hotelId, roomTypeId, date) < qty) {
                return false;
            }
        }
        return true;
    }
}
