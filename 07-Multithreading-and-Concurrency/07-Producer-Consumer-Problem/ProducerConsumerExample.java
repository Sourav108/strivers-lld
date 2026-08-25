import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multithreading and Concurrency: Producer-Consumer Problem
 * 
 * Demonstrates:
 * 1. Thread Coordination using wait() and notifyAll()
 * 2. Bounded Buffer Queue to handle speed disparities between Producers and Consumers
 * 3. Spurious Wakeup protection using while() condition loops
 * 4. Mutual exclusion over shared critical section
 */

public class ProducerConsumerExample {

    // =========================================================================
    // 1. DOMAIN MODEL: CODE SUBMISSION
    // =========================================================================

    static class Submission {
        private static final AtomicInteger idCounter = new AtomicInteger(1001);
        private final int submissionId;
        private final String studentName;
        private final String problemTitle;

        public Submission(String studentName, String problemTitle) {
            this.submissionId = idCounter.getAndIncrement();
            this.studentName = studentName;
            this.problemTitle = problemTitle;
        }

        public int getSubmissionId() { return submissionId; }
        public String getStudentName() { return studentName; }
        public String getProblemTitle() { return problemTitle; }
    }

    // =========================================================================
    // 2. BOUNDED BUFFER QUEUE (Shared Monitor with wait & notifyAll)
    // =========================================================================

    static class SubmissionQueue {
        private final Queue<Submission> queue = new LinkedList<>();
        private final int maxCapacity;

        public SubmissionQueue(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        // Producer Logic: Enqueue submission
        public synchronized void submit(Submission submission) throws InterruptedException {
            // 🔒 Guard against spurious wakeups using while loop!
            while (queue.size() == maxCapacity) {
                System.out.println("   ⏳ [Buffer Full (" + maxCapacity + "/" + maxCapacity + ")] " + 
                                   submission.getStudentName() + " is WAITING to submit #" + submission.getSubmissionId() + "...");
                wait(); // Releases lock and suspends thread
            }

            queue.offer(submission);
            System.out.println("📥 [" + submission.getStudentName() + "] Enqueued Submission #" + 
                               submission.getSubmissionId() + " (" + submission.getProblemTitle() + ") -> Buffer: " + queue.size() + "/" + maxCapacity);

            // Signal waiting consumers that items are available
            notifyAll();
        }

        // Consumer Logic: Dequeue and evaluate submission
        public synchronized Submission consume(String judgeName) throws InterruptedException {
            // 🔒 Guard against empty buffer
            while (queue.isEmpty()) {
                System.out.println("   💤 [" + judgeName + "] Buffer empty. Judge is WAITING for new submissions...");
                wait(); // Releases lock and suspends thread
            }

            Submission sub = queue.poll();
            System.out.println("⚙️ [" + judgeName + "] Dequeued Submission #" + 
                               sub.getSubmissionId() + " (" + sub.getStudentName() + ") -> Buffer: " + queue.size() + "/" + maxCapacity);

            // Signal waiting producers that space is now available
            notifyAll();
            return sub;
        }
    }

    // =========================================================================
    // 🚀 MAIN DRIVER PROGRAM
    // =========================================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 👨‍💻 TUF+ Online Judge: Producer-Consumer Simulation ===");
        System.out.println("Configuration: Bounded Buffer Capacity = 3 | 2 Fast Producers (300ms) | 1 Slow Judge (800ms)\n");

        SubmissionQueue queue = new SubmissionQueue(3);

        // Producer 1: Alice submitting dynamic programming solutions
        Thread producerAlice = new Thread(() -> {
            String[] problems = {"LRU Cache", "Word Break", "Merge K Sorted Lists", "Trapping Rain Water"};
            for (String prob : problems) {
                try {
                    Thread.sleep(250); // Fast submission
                    queue.submit(new Submission("Alice", prob));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Producer-Alice");

        // Producer 2: Bob submitting graph solutions
        Thread producerBob = new Thread(() -> {
            String[] problems = {"Course Schedule", "Alien Dictionary", "Number of Islands"};
            for (String prob : problems) {
                try {
                    Thread.sleep(300); // Fast submission
                    queue.submit(new Submission("Bob", prob));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Producer-Bob");

        // Consumer 1: TUF Judge Worker (compiles, executes sandbox, verifies test cases)
        Thread judgeWorker = new Thread(() -> {
            for (int i = 0; i < 7; i++) {
                try {
                    Submission sub = queue.consume("TUF-Judge-Core-1");
                    Thread.sleep(800); // Simulate time-consuming test evaluation
                    System.out.println("   ✅ [TUF-Judge-Core-1] Verified Submission #" + 
                                       sub.getSubmissionId() + ": ACCEPTED (100% test cases passed)");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Consumer-Judge");

        // Start threads
        judgeWorker.start();
        producerAlice.start();
        producerBob.start();

        // Await completion
        producerAlice.join();
        producerBob.join();
        judgeWorker.join();

        System.out.println("\n===========================================================");
        System.out.println("🎯 All 7 Submissions Evaluated and Processed with Zero Data Loss!");
        System.out.println("===========================================================");
    }
}
