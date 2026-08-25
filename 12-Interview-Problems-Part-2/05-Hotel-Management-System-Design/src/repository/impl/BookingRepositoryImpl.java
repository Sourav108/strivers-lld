package repository.impl;

import domain.Booking;
import domain.BookingStatus;
import repository.BookingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public List<Booking> findByUser(String userId) {
        return bookings.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public int countActiveBookingsOnDate(String hotelId, String roomTypeId, LocalDate date) {
        long count = bookings.values().stream()
                .filter(b -> b.getHotelId().equals(hotelId) && b.getRoomTypeId().equals(roomTypeId))
                .filter(b -> b.getBookingStatus() == BookingStatus.HELD ||
                             b.getBookingStatus() == BookingStatus.CONFIRMED ||
                             b.getBookingStatus() == BookingStatus.CHECKED_IN)
                .filter(b -> !date.isBefore(b.getDateRange().getCheckInDate()) && date.isBefore(b.getDateRange().getCheckOutDate()))
                .count();
        return (int) count;
    }

    @Override
    public List<Booking> findExpiredHeldBookings(long nowMillis) {
        return bookings.values().stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.HELD && b.getHoldExpiresAt() < nowMillis)
                .collect(Collectors.toList());
    }
}
