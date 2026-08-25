package controller;

import domain.Booking;
import domain.DateRange;
import domain.RefundDecision;
import service.BookingService;

import java.time.LocalDate;

public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createBooking(String userId, String hotelId, String roomTypeId, DateRange range, Long expectedTotalPrice) {
        return bookingService.createBooking(userId, hotelId, roomTypeId, range, expectedTotalPrice);
    }

    public RefundDecision cancelBooking(String bookingId, String userId, LocalDate cancellationDate) {
        return bookingService.cancelBooking(bookingId, userId, cancellationDate);
    }
}
