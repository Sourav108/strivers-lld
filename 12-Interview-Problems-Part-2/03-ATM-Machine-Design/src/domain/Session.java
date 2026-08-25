package domain;

public class Session {
    private final String id;
    private final String atmId;
    private final String cardId;
    private final String accountId;
    private final long startTime;
    private Long endTime;
    private boolean isActive;
    private String currentTransactionId;

    public Session(String id, String atmId, String cardId, String accountId) {
        this.id = id;
        this.atmId = atmId;
        this.cardId = cardId;
        this.accountId = accountId;
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
    }

    public String getId() { return id; }
    public String getAtmId() { return atmId; }
    public String getCardId() { return cardId; }
    public String getAccountId() { return accountId; }
    public long getStartTime() { return startTime; }
    public Long getEndTime() { return endTime; }
    public boolean isActive() { return isActive; }
    public String getCurrentTransactionId() { return currentTransactionId; }

    public void setCurrentTransactionId(String currentTransactionId) {
        this.currentTransactionId = currentTransactionId;
    }

    public void endSession() {
        this.isActive = false;
        this.endTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Session[" + id + " | ATM: " + atmId + " | Card: " + cardId + " | Active: " + isActive + "]";
    }
}
