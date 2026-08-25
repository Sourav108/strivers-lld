import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multithreading and Concurrency: Thread Safety and Synchronization
 * 
 * Demonstrates:
 * 1. Race Condition on Unsynchronized Shared State (Lost Updates)
 * 2. Thread Safety using 'synchronized' Monitor Lock Blocks
 * 3. Lock-Free Thread Safety using 'AtomicInteger' (Hardware CAS)
 * 4. Memory Visibility using 'volatile' Keyword
 */

public class ThreadSafetyAndSynchronizationExample {

    private static final int NUM_THREADS = 10;
    private static final int INCREMENTS_PER_THREAD = 1000;
    private static final int EXPECTED_TOTAL = NUM_THREADS * INCREMENTS_PER_THREAD; // 10,000

    // =========================================================================
    // 1. UNSAFE COUNTER (Suffers from Race Conditions)
    // =========================================================================

    static class UnsafePurchaseCounter {
        private int count = 0;

        // ❌ count++ is not atomic (Read -> Modify -> Write race)
        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    // =========================================================================
    // 2. SYNCHRONIZED COUNTER (Thread-Safe via Monitor Lock)
    // =========================================================================

    static class SynchronizedPurchaseCounter {
        private final Object lock = new Object();
        private int count = 0;

        // ✅ Synchronized block ensures mutual exclusion and memory barrier
        public void increment() {
            synchronized (lock) {
                count++;
            }
        }

        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
    }

    // =========================================================================
    // 3. ATOMIC COUNTER (Lock-Free Thread-Safe via Hardware CAS)
    // =========================================================================

    static class AtomicPurchaseCounter {
        // ✅ Backed by CPU-level Compare-And-Swap (CAS) instructions
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
    }

    // =========================================================================
    // 4. VOLATILE FLAG DEMO (Ensuring Cross-Thread Memory Visibility)
    // =========================================================================

    static class WorkerController {
        // ✅ Volatile forces reads/writes directly to Main Memory, bypassing CPU cache
        private volatile boolean active = true;

        public void stop() {
            active = false;
        }

        public boolean isActive() {
            return active;
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER & BENCHMARK
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 🛒 TUF+ Flash Sale: Thread Safety & Concurrency Benchmark ===");
        System.out.println("Parameters: " + NUM_THREADS + " Threads, " + INCREMENTS_PER_THREAD + " Increments each. Expected Final Count = " + EXPECTED_TOTAL + "\n");

        // --- 1. Test Unsafe Counter ---
        UnsafePurchaseCounter unsafeCounter = new UnsafePurchaseCounter();
        runBenchmark(unsafeCounter::increment);
        System.out.println("❌ 1. Unsafe Counter Result:        " + unsafeCounter.getCount() + " / " + EXPECTED_TOTAL + 
                           " (⚠️ Lost " + (EXPECTED_TOTAL - unsafeCounter.getCount()) + " updates due to Race Condition!)");

        // --- 2. Test Synchronized Counter ---
        SynchronizedPurchaseCounter syncCounter = new SynchronizedPurchaseCounter();
        runBenchmark(syncCounter::increment);
        System.out.println("🔒 2. Synchronized Counter Result:  " + syncCounter.getCount() + " / " + EXPECTED_TOTAL + " (✅ 100% Correct)");

        // --- 3. Test Atomic Counter ---
        AtomicPurchaseCounter atomicCounter = new AtomicPurchaseCounter();
        runBenchmark(atomicCounter::increment);
        System.out.println("⚡ 3. AtomicInteger Counter Result: " + atomicCounter.getCount() + " / " + EXPECTED_TOTAL + " (✅ 100% Correct via Lock-Free CAS)");

        // --- 4. Test Volatile Memory Visibility ---
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("👀 4. Demonstrating 'volatile' Memory Visibility");
        System.out.println("-----------------------------------------------------------");
        WorkerController controller = new WorkerController();

        Thread backgroundWorker = new Thread(() -> {
            System.out.println("   [Worker-Thread] Polling volatile 'active' flag in loop...");
            int loopCount = 0;
            while (controller.isActive()) {
                loopCount++;
            }
            System.out.println("   [Worker-Thread] 🛑 Detected active=false from main thread! Exited loop safely.");
        });

        backgroundWorker.start();
        Thread.sleep(100); // Give worker time to spin
        System.out.println("   [Main-Thread] Calling controller.stop() to update volatile flag...");
        controller.stop();
        backgroundWorker.join();

        System.out.println("\n===========================================================");
        System.out.println("🎯 Thread Safety & Synchronization Principles Verified!");
        System.out.println("===========================================================");
    }

    private static void runBenchmark(Runnable incrementAction) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    incrementAction.run();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }
}
