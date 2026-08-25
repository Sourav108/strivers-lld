import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Creational Design Patterns: Singleton Pattern
 * 
 * Guarantees a class has only one instance and provides a global access point to it.
 * 
 * Demonstrates:
 * 1. ❌ Bad Design: Non-Thread-Safe Lazy Initialization (creates duplicate instances under concurrency)
 * 2. Approach 1: Eager Initialization (Early Loading)
 * 3. Approach 2: Synchronized Method (Thread-Safe, but slow)
 * 4. Approach 3: Double-Checked Locking (DCL with volatile)
 * 5. Approach 4: Bill Pugh Static Inner Holder (Best Practice)
 * 6. Approach 5: Enum Singleton (Protects against Reflection & Serialization)
 */

// =========================================================================
// ❌ 1. BAD DESIGN: Non-Thread-Safe Lazy Singleton
// Multiple threads calling getInstance() simultaneously can instantiate multiple objects.
// =========================================================================
class BadLazyLogger {
    private static BadLazyLogger instance;

    private BadLazyLogger() {
        // Simulating expensive initialization
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
    }

    public static BadLazyLogger getInstance() {
        if (instance == null) {
            instance = new BadLazyLogger();
        }
        return instance;
    }
}

// =========================================================================
// 2. APPROACH 1: Eager Loading (Early Initialization)
// Thread-safe via ClassLoader, but instantiates eagerly on startup.
// =========================================================================
class EagerLogger {
    private static final EagerLogger INSTANCE = new EagerLogger();

    private EagerLogger() {}

    public static EagerLogger getInstance() {
        return INSTANCE;
    }

    public void log(String message) {
        System.out.println("[EagerLogger] " + message);
    }
}

// =========================================================================
// 3. APPROACH 2: Synchronized Method
// Thread-safe, but every call acquires a lock causing high overhead in high concurrency.
// =========================================================================
class SynchronizedLogger {
    private static SynchronizedLogger instance;

    private SynchronizedLogger() {}

    public static synchronized SynchronizedLogger getInstance() {
        if (instance == null) {
            instance = new SynchronizedLogger();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[SynchronizedLogger] " + message);
    }
}

// =========================================================================
// 4. APPROACH 3: Double-Checked Locking (DCL)
// Synchronizes ONLY during first-time instantiation.
// Note: 'volatile' is mandatory to prevent JVM instruction reordering!
// =========================================================================
class DoubleCheckedLockingLogger {
    private static volatile DoubleCheckedLockingLogger instance;

    private DoubleCheckedLockingLogger() {}

    public static DoubleCheckedLockingLogger getInstance() {
        if (instance == null) {                         // 1st Check (No Lock)
            synchronized (DoubleCheckedLockingLogger.class) {
                if (instance == null) {                 // 2nd Check (With Lock)
                    instance = new DoubleCheckedLockingLogger();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[DCL Logger] " + message);
    }
}

// =========================================================================
// 5. APPROACH 4: Bill Pugh Singleton (Static Inner Holder) ⭐ RECOMMENDED
// Leverages ClassLoader lazy loading. Zero synchronization overhead!
// =========================================================================
class BillPughLogger {
    private BillPughLogger() {}

    // Inner static class is only loaded when BillPughLogger.getInstance() is invoked
    private static class Holder {
        private static final BillPughLogger INSTANCE = new BillPughLogger();
    }

    public static BillPughLogger getInstance() {
        return Holder.INSTANCE;
    }

    public void log(String message) {
        System.out.println("[BillPugh Logger] " + message);
    }
}

// =========================================================================
// 6. APPROACH 5: Enum Singleton (Effective Java Recommendation)
// Automatically handles Serialization and protects against Reflection attacks.
// =========================================================================
enum EnumLogger {
    INSTANCE;

    public void log(String message) {
        System.out.println("[Enum Logger] " + message);
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class SingletonExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Verifying Single Instance in Single Thread ===");
        BillPughLogger logger1 = BillPughLogger.getInstance();
        BillPughLogger logger2 = BillPughLogger.getInstance();
        System.out.println("Logger1 HashCode: " + logger1.hashCode());
        System.out.println("Logger2 HashCode: " + logger2.hashCode());
        System.out.println("Are both instances identical? " + (logger1 == logger2));

        System.out.println("\n=== 2. Multi-Threaded Concurrency Test for Double-Checked Locking ===");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 5; i++) {
            final int threadId = i;
            executor.submit(() -> {
                DoubleCheckedLockingLogger dcl = DoubleCheckedLockingLogger.getInstance();
                System.out.println("Thread #" + threadId + " acquired DCL instance: HashCode=" + dcl.hashCode());
            });
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("\n=== 3. Using Enum Singleton ===");
        EnumLogger enumLogger = EnumLogger.INSTANCE;
        enumLogger.log("Application started successfully with Enum Singleton!");
    }
}
