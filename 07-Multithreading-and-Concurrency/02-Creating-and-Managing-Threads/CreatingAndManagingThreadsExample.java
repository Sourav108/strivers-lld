import java.util.concurrent.*;

/**
 * Multithreading and Concurrency: Creating and Managing Threads in Java
 * 
 * Demonstrates:
 * 1. Extending Thread class vs Implementing Runnable (Fire-and-Forget)
 * 2. Implementing Callable<V> + Future<V> for Value-Returning Asynchronous Tasks
 * 3. Wrapping Callable inside FutureTask<V>
 * 4. Observing Thread Lifecycle States (NEW, RUNNABLE, TIMED_WAITING, TERMINATED)
 */

public class CreatingAndManagingThreadsExample {

    // =========================================================================
    // 1. RUNNABLE TASKS (Fire-and-Forget - No Return Value)
    // =========================================================================

    static class SMSTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("   [SMS-Thread: " + Thread.currentThread().getName() + "] ⏳ Sending SMS notification (2000ms)...");
                Thread.sleep(2000);
                System.out.println("   [SMS-Thread: " + Thread.currentThread().getName() + "] 📱 SMS Successfully Sent to Customer!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class EmailTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("   [Email-Thread: " + Thread.currentThread().getName() + "] ⏳ Dispatching HTML Invoice (3000ms)...");
                Thread.sleep(3000);
                System.out.println("   [Email-Thread: " + Thread.currentThread().getName() + "] 📧 Email Invoice Successfully Delivered!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // =========================================================================
    // 2. CALLABLE TASK (Returns a Result & Throws Checked Exceptions)
    // =========================================================================

    static class ETACalculationTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("   [ETA-Worker: " + Thread.currentThread().getName() + "] ⏳ Computing live GPS traffic & delivery ETA (5000ms)...");
            Thread.sleep(5000);
            System.out.println("   [ETA-Worker: " + Thread.currentThread().getName() + "] 🛵 ETA calculation completed.");
            return "25 minutes (Driver assigned: Rahul Kumar, Vehicle: TVS Apache)";
        }
    }

    // =========================================================================
    // 3. DEMO 1: ExecutorService with Runnable & Callable (Future.get())
    // =========================================================================

    public static void runExecutorServiceDemo() throws Exception {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🚀 DEMO 1: Concurrency via ExecutorService (Runnable + Callable)");
        System.out.println("-----------------------------------------------------------");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        long start = System.currentTimeMillis();

        // Submit Fire-and-Forget Runnable tasks
        executor.submit(new SMSTask());
        executor.submit(new EmailTask());

        // Submit Value-Returning Callable task
        Future<String> etaFuture = executor.submit(new ETACalculationTask());

        System.out.println("⏳ Main thread doing other work while tasks run asynchronously in background...");
        System.out.println("⏳ Waiting for ETA calculation result via Future.get()...");

        // Blocks until the Callable finishes
        String etaResult = etaFuture.get();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("\n🎉 Result received from Future: " + etaResult);
        System.out.println("⏱️ Total Elapsed Time: " + elapsed + " ms (Expected ~5000ms max instead of 10000ms sequential)");

        executor.shutdown();
    }

    // =========================================================================
    // 4. DEMO 2: FutureTask with raw Thread
    // =========================================================================

    public static void runFutureTaskDemo() throws Exception {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🚀 DEMO 2: Executing Callable via FutureTask & Thread");
        System.out.println("-----------------------------------------------------------");

        // Wrap Callable in FutureTask (implements both Runnable and Future)
        FutureTask<String> etaFutureTask = new FutureTask<>(new ETACalculationTask());

        Thread thread = new Thread(etaFutureTask, "FutureTask-Thread");
        thread.start();

        System.out.println("⏳ FutureTask started in standalone thread. Calling get()...");
        String result = etaFutureTask.get();
        System.out.println("✅ FutureTask returned: " + result);
    }

    // =========================================================================
    // 5. DEMO 3: Inspecting Thread Lifecycle States (NEW, RUNNABLE, TIMED_WAITING, TERMINATED)
    // =========================================================================

    public static void runThreadLifecycleDemo() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🔄 DEMO 3: Observing Thread Lifecycle State Transitions");
        System.out.println("-----------------------------------------------------------");

        Thread monitoredThread = new Thread(() -> {
            try {
                // RUNNING / RUNNABLE
                Thread.sleep(1000); // Transitions to TIMED_WAITING
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Lifecycle-Monitored-Thread");

        // 1. State: NEW
        System.out.println("1️⃣ After instantiation:        Thread State = " + monitoredThread.getState());

        // 2. State: RUNNABLE
        monitoredThread.start();
        System.out.println("2️⃣ Immediately after start():   Thread State = " + monitoredThread.getState());

        // 3. State: TIMED_WAITING (Give worker a moment to enter sleep)
        Thread.sleep(200);
        System.out.println("3️⃣ While sleeping inside run(): Thread State = " + monitoredThread.getState());

        // 4. State: TERMINATED (Wait for completion)
        monitoredThread.join();
        System.out.println("4️⃣ After thread completes:     Thread State = " + monitoredThread.getState());
    }

    // =========================================================================
    // 🚀 MAIN DRIVER
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("=== 🧵 Creating and Managing Threads in Java ===");

        // Run Demos
        runExecutorServiceDemo();
        runFutureTaskDemo();
        runThreadLifecycleDemo();

        System.out.println("\n===========================================================");
        System.out.println("🎯 All Thread Management Scenarios Successfully Demonstrated!");
        System.out.println("===========================================================");
    }
}
