package repository;

import domain.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(String transactionId);
    List<Transaction> findBySession(String sessionId);
}
