package service;

import domain.Booking;
import repository.BookingRepository;

import java.util.List;

public class UserService {
    private final BookingRepository bookingRepository;

    public UserService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> listUserBookings(String userId) {
        return bookingRepository.findByUser(userId);
    }
}
