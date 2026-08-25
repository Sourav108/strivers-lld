/**
 * Multithreading and Concurrency: Fundamentals
 * 
 * Core Concepts:
 * - Program: Static instructions on disk.
 * - Process: Running instance of a program in isolated memory.
 * - Thread: Lightweight unit of execution within a process sharing heap memory.
 * - Concurrency vs Parallelism: Interleaved execution vs true simultaneous multi-core execution.
 */

public class MultithreadingFundamentalsExample {

    // =========================================================================
    // 1. SIMULATED I/O TASKS
    // =========================================================================

    private static void fetchVideoMetadata() {
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ⏳ Fetching video metadata (300ms)...");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ✅ Metadata loaded (Resolution: 4K, Bitrate: 15Mbps).");
    }

    private static void downloadVideoStream() {
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ⏳ Downloading video chunks (500ms)...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ✅ Video buffer ready (Buffered 30 seconds).");
    }

    private static void loadSubtitleTrack() {
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ⏳ Loading English subtitles (200ms)...");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("   [Thread: " + Thread.currentThread().getName() + "] ✅ Subtitles synchronized.");
    }

    // =========================================================================
    // 2. SEQUENTIAL EXECUTION (Blocking - Cumulative Latency)
    // =========================================================================

    public static void runSequentialPipeline() {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🐌 SCENARIO 1: SEQUENTIAL EXECUTION (Single Main Thread)");
        System.out.println("-----------------------------------------------------------");

        long startTime = System.currentTimeMillis();

        fetchVideoMetadata();
        downloadVideoStream();
        loadSubtitleTrack();

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ Total Sequential Execution Time: " + elapsedTime + " ms (Expected ~1000ms)");
    }

    // =========================================================================
    // 3. CONCURRENT / PARALLEL EXECUTION (Multithreaded - Overlapped Latency)
    // =========================================================================

    public static void runConcurrentPipeline() throws InterruptedException {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("⚡ SCENARIO 2: CONCURRENT EXECUTION (Multithreaded Parallelism)");
        System.out.println("-----------------------------------------------------------");

        long startTime = System.currentTimeMillis();

        // Create independent worker threads
        Thread metadataThread = new Thread(MultithreadingFundamentalsExample::fetchVideoMetadata, "Metadata-Worker");
        Thread videoThread = new Thread(MultithreadingFundamentalsExample::downloadVideoStream, "VideoStream-Worker");
        Thread subtitleThread = new Thread(MultithreadingFundamentalsExample::loadSubtitleTrack, "Subtitle-Worker");

        // Start all threads concurrently
        metadataThread.start();
        videoThread.start();
        subtitleThread.start();

        // Wait for all threads to finish (Thread Join Barrier)
        metadataThread.join();
        videoThread.join();
        subtitleThread.join();

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ Total Concurrent Execution Time: " + elapsedTime + " ms (Expected ~500ms - bounded by slowest task)");
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 🎬 Video Streaming Platform: Multithreading Benchmark ===");

        // System CPU Core inspection
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("💻 System Information:");
        System.out.println("   - Available CPU Logical Cores: " + availableProcessors);
        System.out.println("   - Main Thread: " + Thread.currentThread().getName());

        // 1. Run Sequential Pipeline
        runSequentialPipeline();

        // 2. Run Concurrent Pipeline
        runConcurrentPipeline();

        System.out.println("\n===========================================================");
        System.out.println("🎯 Performance Summary: Concurrency drastically reduced startup latency!");
        System.out.println("===========================================================");
    }
}
