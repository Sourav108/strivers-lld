package domain;

import domain.exception.InsufficientFundsException;

public class Account {
    private final String id;
    private final String holderName;
    private long balanceMinorUnits;
    private final long dailyWithdrawalLimitMinor;
    private long dailyWithdrawalUsedMinor;
    private boolean isActive;

    public Account(String id, String holderName, long initialBalanceMinor, long dailyLimitMinor) {
        this.id = id;
        this.holderName = holderName;
        this.balanceMinorUnits = initialBalanceMinor;
        this.dailyWithdrawalLimitMinor = dailyLimitMinor;
        this.dailyWithdrawalUsedMinor = 0;
        this.isActive = true;
    }

    public String getId() { return id; }
    public String getHolderName() { return holderName; }
    public synchronized long getBalanceMinorUnits() { return balanceMinorUnits; }
    public double getBalanceRupees() { return balanceMinorUnits / 100.0; }
    public long getDailyWithdrawalLimitMinor() { return dailyWithdrawalLimitMinor; }
    public long getDailyWithdrawalUsedMinor() { return dailyWithdrawalUsedMinor; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public synchronized void deposit(long amountMinorUnits) {
        if (amountMinorUnits <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balanceMinorUnits += amountMinorUnits;
    }

    public synchronized void withdraw(long amountMinorUnits) {
        if (amountMinorUnits <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amountMinorUnits > balanceMinorUnits) {
            throw new InsufficientFundsException("❌ Insufficient account balance. Available: ₹" + (balanceMinorUnits / 100.0));
        }
        if (dailyWithdrawalUsedMinor + amountMinorUnits > dailyWithdrawalLimitMinor) {
            throw new InsufficientFundsException("❌ Daily withdrawal limit exceeded. Limit: ₹" +
                    (dailyWithdrawalLimitMinor / 100.0) + ", Already used: ₹" + (dailyWithdrawalUsedMinor / 100.0));
        }
        this.balanceMinorUnits -= amountMinorUnits;
        this.dailyWithdrawalUsedMinor += amountMinorUnits;
    }

    @Override
    public String toString() {
        return "Account[" + id + " | " + holderName + " | Bal: ₹" + (balanceMinorUnits / 100.0) + "]";
    }
}
