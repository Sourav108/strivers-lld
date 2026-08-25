package repository;

import domain.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String bookingId);
    List<Booking> findByUser(String userId);
    List<Booking> findAll();
    int countActiveBookingsOnDate(String hotelId, String roomTypeId, LocalDate date);
    List<Booking> findExpiredHeldBookings(long nowMillis);
}
