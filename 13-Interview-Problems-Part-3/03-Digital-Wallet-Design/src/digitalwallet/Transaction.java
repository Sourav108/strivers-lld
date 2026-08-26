package digitalwallet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Immutable audit trail record of a financial transaction.
 * Stores monetary amounts in minor units (long) to eliminate floating-point rounding errors.
 */
public class Transaction {
    private final String transactionId;
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final long amountMinor;
    private final TransactionType type;
    private final TransactionStatus status;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(String fromAccountNumber, String toAccountNumber, long amountMinor,
                       TransactionType type, TransactionStatus status, String description) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amountMinor = amountMinor;
        this.type = type;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts minor currency units to human-readable major currency format.
     */
    public String getFormattedAmount() {
        return String.format("%.2f TUF", amountMinor / 100.0);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s | Type: %-8s | Status: %-9s | Amount: %10s | From: %-10s -> To: %-10s | %s",
                timestamp.format(formatter), transactionId, type, status, getFormattedAmount(),
                (fromAccountNumber != null ? fromAccountNumber : "EXTERNAL"),
                (toAccountNumber != null ? toAccountNumber : "EXTERNAL"),
                description);
    }
}
