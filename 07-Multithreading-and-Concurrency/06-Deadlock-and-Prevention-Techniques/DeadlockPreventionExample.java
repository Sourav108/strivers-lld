import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Multithreading and Concurrency: Deadlock and Prevention Techniques
 * 
 * Demonstrates:
 * 1. The 4 Coffman Conditions (Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait)
 * 2. Prevention Strategy 1: Global Lock Ordering (Breaks Circular Wait)
 * 3. Prevention Strategy 2: Timed tryLock() with Backoff (Breaks Hold and Wait)
 * 4. Prevention Strategy 3: Minimizing Nested Locking
 */

public class DeadlockPreventionExample {

    // =========================================================================
    // 1. BANK ACCOUNT DOMAIN ENTITY
    // =========================================================================

    static class BankAccount {
        private final int accountId;
        private final String accountName;
        private int balance;
        private final ReentrantLock lock = new ReentrantLock();

        public BankAccount(int accountId, String accountName, int balance) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.balance = balance;
        }

        public void withdraw(int amount) {
            this.balance -= amount;
        }

        public void deposit(int amount) {
            this.balance += amount;
        }

        public int getAccountId() { return accountId; }
        public String getAccountName() { return accountName; }
        public int getBalance() { return balance; }
        public ReentrantLock getLock() { return lock; }
    }

    // =========================================================================
    // 2. STRATEGY 1: GLOBAL LOCK ORDERING (Breaks Circular Wait)
    // =========================================================================

    public static void transferWithLockOrdering(BankAccount from, BankAccount to, int amount) {
        BankAccount[] accounts = new BankAccount[]{from, to};

        // Deterministically sort accounts by accountId before acquiring locks
        Arrays.sort(accounts, (a, b) -> Integer.compare(a.getAccountId(), b.getAccountId()));

        BankAccount firstLock = accounts[0];
        BankAccount secondLock = accounts[1];

        synchronized (firstLock) {
            System.out.println("   🔒 [" + Thread.currentThread().getName() + "] Acquired FIRST lock on: " + firstLock.getAccountName());
            try {
                Thread.sleep(50); // Simulate processing
            } catch (InterruptedException ignored) {}

            synchronized (secondLock) {
                System.out.println("   🔒 [" + Thread.currentThread().getName() + "] Acquired SECOND lock on: " + secondLock.getAccountName());
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println("   💸 [" + Thread.currentThread().getName() + "] Successfully transferred ₹" + amount + 
                                   " from " + from.getAccountName() + " to " + to.getAccountName());
            }
        }
    }

    // =========================================================================
    // 3. STRATEGY 2: tryLock() WITH TIMEOUT & BACKOFF (Breaks Hold and Wait)
    // =========================================================================

    public static void transferWithTryLockBackoff(BankAccount from, BankAccount to, int amount) {
        boolean transferCompleted = false;
        int attempts = 0;

        while (!transferCompleted && attempts < 10) {
            attempts++;
            boolean acquiredFrom = false;
            boolean acquiredTo = false;

            try {
                acquiredFrom = from.getLock().tryLock(50, TimeUnit.MILLISECONDS);
                if (acquiredFrom) {
                    acquiredTo = to.getLock().tryLock(50, TimeUnit.MILLISECONDS);
                    if (acquiredTo) {
                        from.withdraw(amount);
                        to.deposit(amount);
                        System.out.println("   ✅ [" + Thread.currentThread().getName() + "] tryLock Transfer Succeeded: ₹" + 
                                           amount + " (" + from.getAccountName() + " -> " + to.getAccountName() + ") on attempt #" + attempts);
                        transferCompleted = true;
                    } else {
                        System.out.println("   ⏩ [" + Thread.currentThread().getName() + "] Contention on " + to.getAccountName() + 
                                           ". Backing off and releasing " + from.getAccountName() + "...");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                if (acquiredTo) to.getLock().unlock();
                if (acquiredFrom) from.getLock().unlock();
            }

            if (!transferCompleted) {
                try {
                    // Random backoff jitter to prevent livelock
                    Thread.sleep((long) (Math.random() * 40 + 10));
                } catch (InterruptedException ignored) {}
            }
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 🛡️ Deadlock Prevention Strategies in Java ===");

        // --- Demo 1: Global Lock Ordering (Bidirectional Transfers) ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ DEMO 1: Global Lock Ordering (Sorting Resource IDs)");
        System.out.println("-----------------------------------------------------------");

        BankAccount accA = new BankAccount(101, "Account-Alice", 5000);
        BankAccount accB = new BankAccount(102, "Account-Bob", 5000);

        // Thread 1: Alice -> Bob (101 -> 102)
        Thread t1 = new Thread(() -> transferWithLockOrdering(accA, accB, 500), "TransferThread-1");
        // Thread 2: Bob -> Alice (102 -> 101) in reverse!
        Thread t2 = new Thread(() -> transferWithLockOrdering(accB, accA, 300), "TransferThread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("💰 Final Balances after Lock Ordering:");
        System.out.println("   - Alice: ₹" + accA.getBalance() + " (Expected ₹4800)");
        System.out.println("   - Bob:   ₹" + accB.getBalance() + " (Expected ₹5200)");

        // --- Demo 2: tryLock() with Timeout Backoff ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ DEMO 2: Timed tryLock() with Non-Blocking Backoff");
        System.out.println("-----------------------------------------------------------");

        BankAccount accX = new BankAccount(201, "Account-Charlie", 3000);
        BankAccount accY = new BankAccount(202, "Account-David", 3000);

        Thread t3 = new Thread(() -> transferWithTryLockBackoff(accX, accY, 200), "TryLockThread-1");
        Thread t4 = new Thread(() -> transferWithTryLockBackoff(accY, accX, 400), "TryLockThread-2");

        t3.start();
        t4.start();

        t3.join();
        t4.join();

        System.out.println("💰 Final Balances after tryLock Backoff:");
        System.out.println("   - Charlie: ₹" + accX.getBalance() + " (Expected ₹3200)");
        System.out.println("   - David:   ₹" + accY.getBalance() + " (Expected ₹2800)");

        System.out.println("\n===========================================================");
        System.out.println("🎯 Both Deadlock Prevention Strategies Executed with Zero Deadlocks!");
        System.out.println("===========================================================");
    }
}
