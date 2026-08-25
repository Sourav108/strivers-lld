package domain;

import java.util.EnumMap;
import java.util.Map;

public class Transaction {
    private final String id;
    private final String atmId;
    private final String sessionId;
    private final String accountId;
    private final TransactionType type;
    private final long amountMinorUnits;
    private TransactionStatus status;
    private final Map<Denomination, Integer> dispensedNotes = new EnumMap<>(Denomination.class);
    private final Map<Denomination, Integer> depositedNotes = new EnumMap<>(Denomination.class);
    private final long createdAt;

    public Transaction(String id, String atmId, String sessionId, String accountId, TransactionType type, long amountMinorUnits) {
        this.id = id;
        this.atmId = atmId;
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.type = type;
        this.amountMinorUnits = amountMinorUnits;
        this.status = TransactionStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getAtmId() { return atmId; }
    public String getSessionId() { return sessionId; }
    public String getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public long getAmountMinorUnits() { return amountMinorUnits; }
    public double getAmountRupees() { return amountMinorUnits / 100.0; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public Map<Denomination, Integer> getDispensedNotes() { return dispensedNotes; }
    public Map<Denomination, Integer> getDepositedNotes() { return depositedNotes; }
    public long getCreatedAt() { return createdAt; }

    public void setDispensedNotes(Map<Denomination, Integer> notes) {
        this.dispensedNotes.clear();
        if (notes != null) this.dispensedNotes.putAll(notes);
    }

    public void setDepositedNotes(Map<Denomination, Integer> notes) {
        this.depositedNotes.clear();
        if (notes != null) this.depositedNotes.putAll(notes);
    }

    @Override
    public String toString() {
        return "Transaction[" + id + " | " + type + " | ₹" + (amountMinorUnits / 100.0) + " | Status: " + status + "]";
    }
}
