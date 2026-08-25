package repository;

import domain.Transaction;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(String transactionId);
    Optional<Transaction> findByBookingId(String bookingId);
    Optional<Transaction> findByProviderRef(String providerRef);
}
