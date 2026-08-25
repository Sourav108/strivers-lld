package service;

import domain.Booking;
import domain.BookingStatus;
import domain.Transaction;
import domain.TransactionStatus;
import repository.BookingRepository;
import repository.TransactionRepository;

import java.util.UUID;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final BookingStateHandler stateHandler;

    public TransactionService(TransactionRepository transactionRepository,
                              BookingRepository bookingRepository,
                              BookingStateHandler stateHandler) {
        this.transactionRepository = transactionRepository;
        this.bookingRepository = bookingRepository;
        this.stateHandler = stateHandler;
    }

    public synchronized Transaction initiateTransaction(Booking booking) {
        if (!stateHandler.canInitiateTransaction(booking)) {
            throw new IllegalStateException("❌ Cannot initiate payment for booking in state: " + booking.getBookingStatus());
        }

        // Two-Phase Step 2: Transition CREATED -> HELD (locks inventory)
        stateHandler.transition(booking, BookingStatus.HELD);
        long holdTtl = System.currentTimeMillis() + (10 * 60 * 1000); // 10 minute hold TTL
        booking.setHoldExpiresAt(holdTtl);
        bookingRepository.save(booking);

        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String providerRef = "RAZORPAY_REF_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Transaction transaction = new Transaction(txId, booking.getId(), booking.getTotalAmountMinor(), "INR", providerRef);
        transactionRepository.save(transaction);

        System.out.println("💳 [Payment Initiated] Booking #" + booking.getId() + " is now HELD (Inventory Locked). Tx: " + txId);
        return transaction;
    }

    public synchronized void handleCallback(String providerRef, TransactionStatus status) {
        Transaction transaction = transactionRepository.findByProviderRef(providerRef)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ref '" + providerRef + "' not found."));

        Booking booking = bookingRepository.findById(transaction.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for transaction: " + transaction.getId()));

        if (status == TransactionStatus.COMPLETED) {
            transaction.markCompleted();
            stateHandler.transition(booking, BookingStatus.CONFIRMED);
            booking.setPaymentStatus(TransactionStatus.COMPLETED);
            System.out.println("✅ [Payment Success] Booking #" + booking.getId() + " CONFIRMED! Payment ID: " + transaction.getId());
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
            stateHandler.transition(booking, BookingStatus.CANCELLED);
            booking.setPaymentStatus(TransactionStatus.FAILED);
            System.out.println("❌ [Payment Failed] Booking #" + booking.getId() + " CANCELLED and inventory restored.");
        }

        transactionRepository.save(transaction);
        bookingRepository.save(booking);
    }

    public synchronized void issueRefund(String bookingId, long amountMinor) {
        transactionRepository.findByBookingId(bookingId).ifPresent(tx -> {
            tx.markRefunded();
            transactionRepository.save(tx);
            System.out.println("💸 [Refund Issued] Processed refund of ₹" + (amountMinor / 100.0) + " for Booking #" + bookingId);
        });
    }
}
