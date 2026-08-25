package repository.impl;

import domain.Transaction;
import repository.TransactionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public List<Transaction> findBySession(String sessionId) {
        return transactions.values().stream()
                .filter(t -> t.getSessionId().equals(sessionId))
                .collect(Collectors.toList());
    }
}
