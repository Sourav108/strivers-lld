package service;

import domain.*;
import repository.BookingRepository;
import repository.CancellationPolicyRepository;
import repository.HotelRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final PolicyService policyService;
    private final TransactionService transactionService;
    private final BookingStateHandler stateHandler;

    public BookingService(BookingRepository bookingRepository,
                          HotelRepository hotelRepository,
                          CancellationPolicyRepository cancellationPolicyRepository,
                          InventoryService inventoryService,
                          PricingService pricingService,
                          PolicyService policyService,
                          TransactionService transactionService,
                          BookingStateHandler stateHandler) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.policyService = policyService;
        this.transactionService = transactionService;
        this.stateHandler = stateHandler;
    }

    public synchronized Booking createBooking(String userId, String hotelId, String roomTypeId,
                                              DateRange range, Long expectedTotalPriceMinor) {
        // 1. Pre-check inventory availability
        boolean available = inventoryService.checkAvailability(hotelId, roomTypeId, range, 1);
        if (!available) {
            throw new IllegalStateException("❌ Room type #" + roomTypeId + " is SOLD OUT for stay: " + range);
        }

        // 2. Compute dynamic nightly rates (Seasonal or Base)
        List<NightlyPrice> rates = pricingService.rateStay(hotelId, roomTypeId, range);
        long totalPriceMinor = pricingService.computeTotal(rates);

        // 3. Price drift protection
        if (expectedTotalPriceMinor != null && expectedTotalPriceMinor != totalPriceMinor) {
            throw new IllegalArgumentException("❌ Price Drift Detected: Expected ₹" + (expectedTotalPriceMinor / 100.0) +
                    " but current dynamic price is ₹" + (totalPriceMinor / 100.0));
        }

        // 4. Two-Phase Step 1: Create CREATED booking (price locked, inventory not yet locked)
        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long holdExpiry = System.currentTimeMillis() + (10 * 60 * 1000); // 10 min window to initiate payment
        Booking booking = new Booking(bookingId, userId, hotelId, roomTypeId, range, rates, totalPriceMinor, holdExpiry);

        bookingRepository.save(booking);
        System.out.println("📝 [Booking Created (Phase 1)] " + booking);
        return booking;
    }

    public synchronized RefundDecision cancelBooking(String bookingId, String userId, LocalDate cancellationDate) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking #" + bookingId + " not found."));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User #" + userId + " is not authorized to cancel booking #" + bookingId);
        }

        if (!stateHandler.canCancel(booking)) {
            throw new IllegalStateException("❌ Booking cannot be cancelled in state: " + booking.getBookingStatus());
        }

        Hotel hotel = hotelRepository.findById(booking.getHotelId()).orElseThrow();
        CancellationPolicy policy = cancellationPolicyRepository.findById(hotel.getCancellationPolicyId()).orElse(null);

        RefundDecision decision = policyService.evaluateCancellation(booking, policy, cancellationDate);

        stateHandler.transition(booking, BookingStatus.CANCELLED);

        if (decision.isRefundable() && decision.getRefundAmountMinor() > 0) {
            booking.setPaymentStatus(TransactionStatus.REFUNDED);
            transactionService.issueRefund(bookingId, decision.getRefundAmountMinor());
        }

        bookingRepository.save(booking);
        System.out.println("❌ [Booking Cancelled] " + decision);
        return decision;
    }

    public synchronized Booking checkIn(String bookingId, String roomId, long checkInTimeUtc) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking #" + bookingId + " not found."));

        if (!stateHandler.canCheckIn(booking)) {
            throw new IllegalStateException("❌ Cannot check in. Booking status is: " + booking.getBookingStatus());
        }

        stateHandler.transition(booking, BookingStatus.CHECKED_IN);
        booking.setAllocatedRoomId(roomId);
        booking.setCheckInTimeUtc(checkInTimeUtc);
        bookingRepository.save(booking);

        System.out.println("🏨 [Guest Checked-In] Booking #" + bookingId + " allocated Room: " + roomId);
        return booking;
    }

    public synchronized Booking checkOut(String bookingId, long checkOutTimeUtc) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking #" + bookingId + " not found."));

        if (!stateHandler.canCheckOut(booking)) {
            throw new IllegalStateException("❌ Cannot check out. Booking status is: " + booking.getBookingStatus());
        }

        stateHandler.transition(booking, BookingStatus.CHECKED_OUT);
        booking.setCheckOutTimeUtc(checkOutTimeUtc);
        bookingRepository.save(booking);

        System.out.println("🚪 [Guest Checked-Out] Booking #" + bookingId + " completed. Inventory released.");
        return booking;
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking #" + bookingId + " not found."));
    }
}
