package digitalwallet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central Facade and orchestrator for the Digital Wallet System.
 * Handles user onboarding, wallet lifecycle, external payment gateway deposits,
 * deadlock-free atomic transfers with ordered locking, and account statements.
 */
public class DigitalWalletSystem {
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<String, Wallet> walletsByAccount = new ConcurrentHashMap<>();
    private final Map<Integer, Wallet> walletsByUser = new ConcurrentHashMap<>();

    private final AtomicInteger userIdCounter = new AtomicInteger(100);
    private final AtomicInteger walletIdCounter = new AtomicInteger(1);

    private final NotificationService notificationService;

    public DigitalWalletSystem() {
        this(new EmailNotificationService());
    }

    public DigitalWalletSystem(NotificationService notificationService) {
        this.notificationService = notificationService != null ? notificationService : new EmailNotificationService();
    }

    /**
     * Registers a new user in the system.
     */
    public User registerUser(String name, String email) {
        int userId = userIdCounter.incrementAndGet();
        User user = new User(userId, name, email);
        users.put(userId, user);
        System.out.println("👤 User registered: " + user);
        return user;
    }

    /**
     * Creates a wallet for a user. Enforces the one-wallet-per-user constraint.
     */
    public synchronized Wallet createWallet(int userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
        if (walletsByUser.containsKey(userId)) {
            throw new IllegalStateException("User " + userId + " already possesses an active wallet: " +
                    walletsByUser.get(userId).getAccountNumber());
        }

        int walletId = walletIdCounter.getAndIncrement();
        String accountNumber = String.format("ACC-%04d", walletId);
        Wallet wallet = new Wallet(walletId, accountNumber, userId);

        walletsByAccount.put(accountNumber, wallet);
        walletsByUser.put(userId, wallet);

        System.out.println("💼 Wallet created: " + wallet);
        return wallet;
    }

    /**
     * Deposits funds into a wallet via a pluggable PaymentGateway (Strategy Pattern).
     */
    public boolean deposit(String accountNumber, long amountMinor, PaymentGateway paymentGateway) {
        Wallet wallet = walletsByAccount.get(accountNumber);
        if (wallet == null) {
            System.out.println("❌ Deposit failed: Wallet " + accountNumber + " not found.");
            return false;
        }

        if (!wallet.isActive()) {
            System.out.println("❌ Deposit failed: Wallet " + accountNumber + " is " + wallet.getStatus());
            return false;
        }

        if (amountMinor <= 0) {
            System.out.println("❌ Deposit failed: Amount must be at least 0.01 TUF (1 minor unit).");
            return false;
        }

        boolean success = paymentGateway.processPayment(accountNumber, amountMinor);
        if (success) {
            synchronized (wallet) {
                wallet.credit(amountMinor);
                Transaction txn = new Transaction(null, accountNumber, amountMinor,
                        TransactionType.DEPOSIT, TransactionStatus.COMPLETED,
                        "Deposit via " + paymentGateway.getName());
                wallet.recordTransaction(txn);

                User user = users.get(wallet.getUserId());
                notificationService.sendNotification(user,
                        "Deposit of " + txn.getFormattedAmount() + " successful. New Balance: " + wallet.getFormattedBalance());
                System.out.println("💰 " + txn);
            }
            return true;
        } else {
            Transaction failedTxn = new Transaction(null, accountNumber, amountMinor,
                    TransactionType.DEPOSIT, TransactionStatus.FAILED,
                    "Deposit failed via " + paymentGateway.getName());
            wallet.recordTransaction(failedTxn);
            System.out.println("❌ " + failedTxn);
            return false;
        }
    }

    /**
     * Transfers funds atomically between two wallets using deterministic lock ordering to prevent deadlocks.
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, long amountMinor, String description) {
        if (fromAccountNumber == null || toAccountNumber == null || fromAccountNumber.equals(toAccountNumber)) {
            System.out.println("❌ Transfer rejected: Cannot transfer to self or null accounts (" +
                    fromAccountNumber + " -> " + toAccountNumber + ")");
            return false;
        }

        if (amountMinor <= 0) {
            System.out.println("❌ Transfer rejected: Amount must be at least 0.01 TUF (1 minor unit).");
            return false;
        }

        Wallet fromWallet = walletsByAccount.get(fromAccountNumber);
        Wallet toWallet = walletsByAccount.get(toAccountNumber);

        if (fromWallet == null || toWallet == null) {
            System.out.println("❌ Transfer rejected: One or both accounts do not exist.");
            return false;
        }

        if (!fromWallet.isActive() || !toWallet.isActive()) {
            System.out.println("❌ Transfer rejected: Accounts must be ACTIVE. (From: " +
                    fromWallet.getStatus() + ", To: " + toWallet.getStatus() + ")");
            return false;
        }

        // Deadlock Prevention: Always acquire locks in deterministic order based on Wallet ID
        Wallet firstLock = fromWallet.getId() < toWallet.getId() ? fromWallet : toWallet;
        Wallet secondLock = fromWallet.getId() < toWallet.getId() ? toWallet : fromWallet;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Re-validate state under synchronized lock
                if (!fromWallet.isActive() || !toWallet.isActive()) {
                    System.out.println("❌ Transfer aborted: Wallet state changed before acquiring locks.");
                    return false;
                }

                if (fromWallet.getBalanceMinor() < amountMinor) {
                    System.out.println("❌ Transfer failed: Insufficient funds in " + fromAccountNumber +
                            ". Available: " + fromWallet.getFormattedBalance() +
                            ", Required: " + String.format("%.2f TUF", amountMinor / 100.0));
                    Transaction failedTxn = new Transaction(fromAccountNumber, toAccountNumber, amountMinor,
                            TransactionType.TRANSFER, TransactionStatus.FAILED,
                            "Failed: Insufficient funds - " + description);
                    fromWallet.recordTransaction(failedTxn);
                    return false;
                }

                // Execute atomic debit and credit
                fromWallet.debit(amountMinor);
                toWallet.credit(amountMinor);

                Transaction txn = new Transaction(fromAccountNumber, toAccountNumber, amountMinor,
                        TransactionType.TRANSFER, TransactionStatus.COMPLETED, description);
                fromWallet.recordTransaction(txn);
                toWallet.recordTransaction(txn);

                User sender = users.get(fromWallet.getUserId());
                User recipient = users.get(toWallet.getUserId());

                notificationService.sendNotification(sender,
                        "Sent " + txn.getFormattedAmount() + " to " + toAccountNumber +
                                ". Remaining Balance: " + fromWallet.getFormattedBalance());
                notificationService.sendNotification(recipient,
                        "Received " + txn.getFormattedAmount() + " from " + fromAccountNumber +
                                ". New Balance: " + toWallet.getFormattedBalance());

                System.out.println("💸 " + txn);
                return true;
            }
        }
    }

    /**
     * Updates wallet administrative status (ACTIVE, SUSPENDED, CLOSED).
     */
    public void setWalletStatus(String accountNumber, WalletStatus status) {
        Wallet wallet = walletsByAccount.get(accountNumber);
        if (wallet != null) {
            synchronized (wallet) {
                wallet.setStatus(status);
                System.out.println("🔒 Wallet " + accountNumber + " status changed to: " + status);
            }
        } else {
            System.out.println("❌ Wallet " + accountNumber + " not found.");
        }
    }

    /**
     * Generates and prints a complete account statement for a wallet.
     */
    public void printAccountStatement(String accountNumber) {
        Wallet wallet = walletsByAccount.get(accountNumber);
        if (wallet == null) {
            System.out.println("❌ Account not found: " + accountNumber);
            return;
        }

        User user = users.get(wallet.getUserId());
        System.out.println("\n==========================================================================================");
        System.out.println("📜 ACCOUNT STATEMENT FOR " + accountNumber + " (" + (user != null ? user.getName() : "Unknown") + ")");
        System.out.println("Current Balance: " + wallet.getFormattedBalance() + " | Status: " + wallet.getStatus());
        System.out.println("------------------------------------------------------------------------------------------");
        if (wallet.getTransactions().isEmpty()) {
            System.out.println("  No transactions recorded.");
        } else {
            for (Transaction txn : wallet.getTransactions()) {
                System.out.println("  " + txn);
            }
        }
        System.out.println("==========================================================================================\n");
    }

    public Wallet getWallet(String accountNumber) {
        return walletsByAccount.get(accountNumber);
    }

    public User getUser(int userId) {
        return users.get(userId);
    }
}
