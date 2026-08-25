import java.util.List;
import java.util.concurrent.*;

/**
 * Multithreading and Concurrency: Thread Pools and the Executor Framework
 * 
 * Demonstrates:
 * 1. Fixed Thread Pool for batch background execution (execute)
 * 2. Asynchronous task execution returning results (submit + Future)
 * 3. Scheduled Thread Pool for periodic background jobs (scheduleAtFixedRate)
 * 4. Production Graceful Shutdown Pattern (shutdown + awaitTermination + shutdownNow)
 */

public class ThreadPoolsAndExecutorsExample {

    // =========================================================================
    // 1. DEMO 1: Fixed Thread Pool with Fire-and-Forget execute(Runnable)
    // =========================================================================

    public static void runFixedThreadPoolDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🚀 DEMO 1: Fixed Thread Pool (4 Workers handling 10 Email Tasks)");
        System.out.println("-----------------------------------------------------------");

        ExecutorService emailExecutor = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 10; i++) {
            final String recipient = "customer" + i + "@example.com";
            emailExecutor.execute(() -> {
                System.out.println("📧 [" + Thread.currentThread().getName() + "] Sending newsletter to: " + recipient);
                try {
                    Thread.sleep(500); // Simulate email transmission
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Graceful shutdown
        emailExecutor.shutdown();
        emailExecutor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("✅ All 10 emails processed by the 4-worker thread pool.");
    }

    // =========================================================================
    // 2. DEMO 2: Ride-Matching Service using submit(Callable) and Future<String>
    // =========================================================================

    public static void runSubmitWithFutureDemo() throws Exception {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🚖 DEMO 2: Asynchronous Task Submission with Future Tracking");
        System.out.println("-----------------------------------------------------------");

        ExecutorService rideService = Executors.newFixedThreadPool(2);

        // Submit Callable task
        Future<String> matchFuture = rideService.submit(() -> {
            System.out.println("🔍 [" + Thread.currentThread().getName() + "] Searching nearest drivers for Rider #902...");
            Thread.sleep(1200); // Simulate geospatial search
            return "Driver Vikram (Rating 4.9, White Hyundai i20) matched!";
        });

        System.out.println("📱 App UI: 'Searching for nearby drivers...' (Main thread is NOT blocked)");

        // Retrieve result via blocking get()
        String matchResult = matchFuture.get(3, TimeUnit.SECONDS);
        System.out.println("🎉 Result: " + matchResult);

        rideService.shutdown();
    }

    // =========================================================================
    // 3. DEMO 3: Scheduled Thread Pool for Periodic Maintenance Tasks
    // =========================================================================

    public static void runScheduledThreadPoolDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("⏰ DEMO 3: Scheduled Executor (Periodic Session Cleanup)");
        System.out.println("-----------------------------------------------------------");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable cleanupTask = () -> {
            System.out.println("🧹 [" + Thread.currentThread().getName() + "] Sweeping database for expired user tokens...");
        };

        // Schedule periodic execution every 500ms starting immediately
        scheduler.scheduleAtFixedRate(cleanupTask, 0, 500, TimeUnit.MILLISECONDS);

        // Allow task to run 3 times
        Thread.sleep(1600);

        scheduler.shutdown();
        scheduler.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("✅ Periodic maintenance scheduler terminated.");
    }

    // =========================================================================
    // 4. DEMO 4: Production Graceful Shutdown Pattern
    // =========================================================================

    public static void runGracefulShutdownDemo() {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🛑 DEMO 4: Production-Grade Graceful Shutdown Pattern");
        System.out.println("-----------------------------------------------------------");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(() -> {
            try {
                System.out.println("⏳ Running long batch report...");
                Thread.sleep(800);
                System.out.println("✅ Long batch report completed.");
            } catch (InterruptedException e) {
                System.out.println("⚠️ Task interrupted during shutdown.");
            }
        });

        // 1. Stop accepting new tasks
        pool.shutdown();
        System.out.println("1️⃣ shutdown() called: Pool is closing down...");

        try {
            // 2. Wait up to 2 seconds for existing tasks to terminate
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
                System.out.println("2️⃣ Timeout expired. Forcing shutdownNow()...");
                List<Runnable> droppedTasks = pool.shutdownNow();
                System.out.println("3️⃣ Dropped " + droppedTasks.size() + " unexecuted tasks.");
            } else {
                System.out.println("2️⃣ All tasks completed gracefully within timeout!");
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("=== ⚙️ Thread Pools & Executor Framework in Java ===");

        runFixedThreadPoolDemo();
        runSubmitWithFutureDemo();
        runScheduledThreadPoolDemo();
        runGracefulShutdownDemo();

        System.out.println("\n===========================================================");
        System.out.println("🎯 Thread Pool & Executor Scenarios Successfully Executed!");
        System.out.println("===========================================================");
    }
}
