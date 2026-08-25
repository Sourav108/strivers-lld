package domain;

public class Transaction {
    private final String id;
    private final String bookingId;
    private final long amountMinor;
    private final String currency;
    private TransactionStatus status;
    private final String providerRef;
    private final long createdAt;
    private Long completedAt;
    private Long refundedAt;

    public Transaction(String id, String bookingId, long amountMinor, String currency, String providerRef) {
        this.id = id;
        this.bookingId = bookingId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.status = TransactionStatus.PENDING;
        this.providerRef = providerRef;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public long getAmountMinor() { return amountMinor; }
    public double getAmountRupees() { return amountMinor / 100.0; }
    public String getCurrency() { return currency; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public String getProviderRef() { return providerRef; }
    public long getCreatedAt() { return createdAt; }
    public Long getCompletedAt() { return completedAt; }
    public Long getRefundedAt() { return refundedAt; }

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = System.currentTimeMillis();
    }

    public void markRefunded() {
        this.status = TransactionStatus.REFUNDED;
        this.refundedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Transaction[" + id + " | Booking: " + bookingId + " | ₹" + (amountMinor / 100.0) + " " + currency + " | Status: " + status + "]";
    }
}
