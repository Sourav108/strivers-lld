package domain;

import domain.exception.CardBlockedException;

public class Card {
    private static final int MAX_PIN_RETRIES = 3;

    private final String id;
    private final String accountId;
    private final String expiry;
    private final String pinHash;
    private boolean isBlocked;
    private int pinRetriesLeft;

    public Card(String id, String accountId, String expiry, String pinHash) {
        this.id = id;
        this.accountId = accountId;
        this.expiry = expiry;
        this.pinHash = pinHash;
        this.isBlocked = false;
        this.pinRetriesLeft = MAX_PIN_RETRIES;
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getExpiry() { return expiry; }
    public boolean isBlocked() { return isBlocked; }
    public int getPinRetriesLeft() { return pinRetriesLeft; }

    public synchronized boolean authenticate(String pin) {
        if (isBlocked) {
            throw new CardBlockedException("🚫 Card #" + id + " is BLOCKED due to too many incorrect PIN attempts.");
        }

        if (pin != null && pin.equals(pinHash)) {
            this.pinRetriesLeft = MAX_PIN_RETRIES; // Reset on success
            return true;
        } else {
            this.pinRetriesLeft--;
            if (this.pinRetriesLeft <= 0) {
                this.isBlocked = true;
                throw new CardBlockedException("🚫 Maximum PIN retries exceeded! Card #" + id + " has been BLOCKED.");
            }
            return false;
        }
    }

    public void blockCard() {
        this.isBlocked = true;
    }

    @Override
    public String toString() {
        return "Card[" + id + " | Acct: " + accountId + " | Blocked: " + isBlocked + " | RetriesLeft: " + pinRetriesLeft + "]";
    }
}
