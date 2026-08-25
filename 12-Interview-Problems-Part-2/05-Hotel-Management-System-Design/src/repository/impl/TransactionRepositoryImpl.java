package repository.impl;

import domain.Transaction;
import repository.TransactionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepositoryImpl implements TransactionRepository {
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    @Override
    public Optional<Transaction> findByBookingId(String bookingId) {
        return transactions.values().stream()
                .filter(t -> t.getBookingId().equals(bookingId))
                .findFirst();
    }

    @Override
    public Optional<Transaction> findByProviderRef(String providerRef) {
        return transactions.values().stream()
                .filter(t -> t.getProviderRef().equals(providerRef))
                .findFirst();
    }
}
