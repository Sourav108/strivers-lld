package service;

import domain.Booking;
import domain.BookingStatus;

public class BookingStateHandler {

    public boolean canTransition(BookingStatus current, BookingStatus newStatus) {
        if (current == null || newStatus == null) return false;
        if (current == newStatus) return true;

        switch (current) {
            case CREATED:
                return newStatus == BookingStatus.HELD || newStatus == BookingStatus.CANCELLED;
            case HELD:
                return newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == BookingStatus.CHECKED_IN || newStatus == BookingStatus.CANCELLED;
            case CHECKED_IN:
                return newStatus == BookingStatus.CHECKED_OUT;
            case CHECKED_OUT:
            case CANCELLED:
            default:
                return false; // Terminal states
        }
    }

    public void transition(Booking booking, BookingStatus newStatus) {
        if (!canTransition(booking.getBookingStatus(), newStatus)) {
            throw new IllegalStateException("❌ Invalid Booking State Transition: Cannot move Booking #" +
                    booking.getId() + " from " + booking.getBookingStatus() + " to " + newStatus);
        }
        booking.setBookingStatus(newStatus);
    }

    public boolean canCancel(Booking booking) {
        BookingStatus status = booking.getBookingStatus();
        return status == BookingStatus.CREATED || status == BookingStatus.HELD || status == BookingStatus.CONFIRMED;
    }

    public boolean canCheckIn(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CONFIRMED;
    }

    public boolean canCheckOut(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CHECKED_IN;
    }

    public boolean canInitiateTransaction(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CREATED;
    }
}
