package digitalwallet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a digital wallet holding funds in minor units (long).
 * Encapsulates balance mutations, status transitions, and audit transaction records.
 */
public class Wallet {
    private final int id;
    private final String accountNumber;
    private final int userId;
    private long balanceMinor;
    private WalletStatus status;
    private final List<Transaction> transactions;

    public Wallet(int id, String accountNumber, int userId) {
        this(id, accountNumber, userId, 0L);
    }

    public Wallet(int id, String accountNumber, int userId, long initialBalanceMinor) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balanceMinor = Math.max(0, initialBalanceMinor);
        this.status = WalletStatus.ACTIVE;
        this.transactions = new ArrayList<>();
    }

    /**
     * Credits funds to the wallet.
     */
    public synchronized void credit(long amountMinor) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Cannot credit wallet " + accountNumber + ". Status is: " + status);
        }
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("Credit amount must be greater than zero.");
        }
        this.balanceMinor += amountMinor;
    }

    /**
     * Debits funds from the wallet after validating sufficient balance.
     */
    public synchronized void debit(long amountMinor) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Cannot debit wallet " + accountNumber + ". Status is: " + status);
        }
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("Debit amount must be greater than zero.");
        }
        if (this.balanceMinor < amountMinor) {
            throw new IllegalStateException("Insufficient funds in wallet " + accountNumber +
                    ". Available: " + getFormattedBalance() +
                    ", Required: " + String.format("%.2f TUF", amountMinor / 100.0));
        }
        this.balanceMinor -= amountMinor;
    }

    /**
     * Adds an audit record to the wallet's transaction history.
     */
    public synchronized void recordTransaction(Transaction transaction) {
        if (transaction != null) {
            this.transactions.add(transaction);
        }
    }

    public synchronized void setStatus(WalletStatus status) {
        this.status = status;
    }

    public synchronized boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getUserId() {
        return userId;
    }

    public synchronized long getBalanceMinor() {
        return balanceMinor;
    }

    public synchronized WalletStatus getStatus() {
        return status;
    }

    public synchronized List<Transaction> getTransactions() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    public synchronized String getFormattedBalance() {
        return String.format("%.2f TUF", balanceMinor / 100.0);
    }

    @Override
    public synchronized String toString() {
        return "Wallet[ID=" + id + ", Account=" + accountNumber + ", UserID=" + userId +
                ", Balance=" + getFormattedBalance() + ", Status=" + status + "]";
    }
}
