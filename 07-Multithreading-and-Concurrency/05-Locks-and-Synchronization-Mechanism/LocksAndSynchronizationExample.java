import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Multithreading and Concurrency: Locks and Synchronization Mechanisms
 * 
 * Demonstrates:
 * 1. ReentrantLock with Timed Acquisition (tryLock) to prevent indefinite blocking
 * 2. ReentrantReadWriteLock for high-throughput read-heavy caches (Concurrent Readers, Exclusive Writer)
 * 3. Semaphore for permit-based concurrency throttling (Max 2-Device Policy)
 */

public class LocksAndSynchronizationExample {

    // =========================================================================
    // 1. DEMO 1: ReentrantLock with Timed tryLock (BookMyShow Scenario)
    // =========================================================================

    static class MovieTicketBookingService {
        private int availableSeats = 1;
        private final ReentrantLock lock = new ReentrantLock();

        public void bookTicket(String user, long waitTimeoutMs, long processingDelayMs) {
            System.out.println("🎟️ [" + user + "] Initiating seat selection...");
            boolean acquired = false;
            try {
                // Wait up to timeout; returns false instead of hanging indefinitely
                acquired = lock.tryLock(waitTimeoutMs, TimeUnit.MILLISECONDS);
                if (acquired) {
                    System.out.println("   🔒 [" + user + "] Acquired lock! Processing seat payment...");
                    if (availableSeats > 0) {
                        Thread.sleep(processingDelayMs); // Simulate payment gateway
                        availableSeats--;
                        System.out.println("   🎉 [" + user + "] Ticket successfully booked! (Remaining seats: " + availableSeats + ")");
                    } else {
                        System.out.println("   ❌ [" + user + "] Sold out! No seats left.");
                    }
                } else {
                    System.out.println("   ⚠️ [" + user + "] Lock timeout! Server busy. Booking session cancelled.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (acquired && lock.isHeldByCurrentThread()) {
                    System.out.println("   🔓 [" + user + "] Releasing seat lock.");
                    lock.unlock();
                }
            }
        }
    }

    public static void runReentrantLockDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🎬 DEMO 1: ReentrantLock with Timeout (BookMyShow)");
        System.out.println("-----------------------------------------------------------");

        MovieTicketBookingService bookingService = new MovieTicketBookingService();

        // User 1 grabs lock and takes 2.0s to complete payment
        Thread user1 = new Thread(() -> bookingService.bookTicket("Alice", 500, 2000), "User-Alice");

        // User 2 arrives 200ms later and is willing to wait max 1.0s before timing out
        Thread user2 = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            bookingService.bookTicket("Bob", 1000, 500);
        }, "User-Bob");

        user1.start();
        user2.start();

        user1.join();
        user2.join();
    }

    // =========================================================================
    // 2. DEMO 2: ReentrantReadWriteLock (Stock Market Price Feed)
    // =========================================================================

    static class StockPriceFeed {
        private double price = 1500.0;
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

        // Reader: Many threads can read simultaneously
        public double readPrice(String readerId) {
            rwLock.readLock().lock();
            try {
                System.out.println("   📈 [Reader: " + readerId + "] Read Live Stock Price: ₹" + price);
                Thread.sleep(300); // Simulate reading time
                return price;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return price;
            } finally {
                rwLock.readLock().unlock();
            }
        }

        // Writer: Single exclusive writer blocks all readers and writers
        public void updatePrice(String writerId, double newPrice) {
            rwLock.writeLock().lock();
            try {
                System.out.println("   ✏️ [WRITER: " + writerId + "] Updating Stock Price from ₹" + price + " to ₹" + newPrice + "...");
                Thread.sleep(600); // Simulate database write
                price = newPrice;
                System.out.println("   ✅ [WRITER: " + writerId + "] Price updated to ₹" + newPrice);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static void runReadWriteLockDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("📊 DEMO 2: ReentrantReadWriteLock (Concurrent Readers, Exclusive Writer)");
        System.out.println("-----------------------------------------------------------");

        StockPriceFeed stockFeed = new StockPriceFeed();
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // 3 Readers read concurrently
        pool.submit(() -> stockFeed.readPrice("MobileApp-1"));
        pool.submit(() -> stockFeed.readPrice("Dashboard-2"));
        pool.submit(() -> stockFeed.readPrice("AnalyticsService-3"));

        // 1 Writer updates price exclusively
        pool.submit(() -> stockFeed.updatePrice("NSE-StockExchange", 1545.50));

        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
    }

    // =========================================================================
    // 3. DEMO 3: Semaphore (TUF+ Max 2-Device Concurrency Policy)
    // =========================================================================

    static class TUFPlusAccountLimiter {
        // Enforces max 2 concurrent active device streams
        private final Semaphore deviceSlots = new Semaphore(2);

        public boolean loginDevice(String deviceName) {
            System.out.println("📱 [" + deviceName + "] Attempting login to TUF+ Account...");
            boolean granted = deviceSlots.tryAcquire();
            if (granted) {
                System.out.println("   🟢 [" + deviceName + "] Login successful. (Active streaming session started)");
                return true;
            } else {
                System.out.println("   ⛔ [" + deviceName + "] Login DENIED! Account streaming limit reached (Max 2 devices allowed).");
                return false;
            }
        }

        public void logoutDevice(String deviceName) {
            System.out.println("   🔴 [" + deviceName + "] Logged out. Releasing device slot...");
            deviceSlots.release();
        }
    }

    public static void runSemaphoreDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🛡️ DEMO 3: Semaphore (Max 2-Device Streaming Quota)");
        System.out.println("-----------------------------------------------------------");

        TUFPlusAccountLimiter account = new TUFPlusAccountLimiter();

        Thread device1 = new Thread(() -> {
            if (account.loginDevice("MacBook-Pro")) {
                try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                account.logoutDevice("MacBook-Pro");
            }
        });

        Thread device2 = new Thread(() -> {
            if (account.loginDevice("iPhone-15")) {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                account.logoutDevice("iPhone-15");
            }
        });

        Thread device3 = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            // Device 3 arrives while Device 1 & 2 are streaming -> Denied!
            account.loginDevice("Smart-TV");
        });

        device1.start();
        device2.start();
        device3.start();

        device1.join();
        device2.join();
        device3.join();
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("=== 🔐 Java Advanced Locks and Synchronization Primitives ===");

        runReentrantLockDemo();
        runReadWriteLockDemo();
        runSemaphoreDemo();

        System.out.println("\n===========================================================");
        System.out.println("🎯 ReentrantLock, ReadWriteLock & Semaphore Successfully Verified!");
        System.out.println("===========================================================");
    }
}
