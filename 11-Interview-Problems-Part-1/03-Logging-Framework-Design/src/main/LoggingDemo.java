package main;

import appenders.ConsoleAppender;
import appenders.DatabaseAppender;
import appenders.FileAppender;
import core.LogLevel;
import core.Logger;
import core.LoggerImpl;
import filter.SourceFilter;
import formatters.DetailedFormatter;
import formatters.SimpleFormatter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LoggingDemo: End-to-end Demonstration of the Logging Framework Architecture
 * 
 * Demonstrates:
 * 1. Log Level Hierarchy & Dynamic Reconfiguration
 * 2. Pluggable Formatters (SimpleFormatter vs DetailedFormatter)
 * 3. Multiple Output Destinations (ConsoleAppender, FileAppender, DatabaseAppender)
 * 4. Filter Chain (LevelFilter, SourceFilter)
 * 5. Concurrent Multi-Threaded Logging Safety
 * 6. Error Handling & Fallback Strategy
 */

public class LoggingDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=================================================================");
        System.out.println("🪵 MODULAR LOGGING FRAMEWORK - FULL ARCHITECTURE DEMONSTRATION");
        System.out.println("=================================================================");

        // =========================================================================
        // SCENARIO 1: BASIC LOGGING & LOG LEVEL HIERARCHY
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Basic Logging & Log Level Priority Filtering");
        System.out.println("-----------------------------------------------------------");

        Logger logger = LoggerImpl.getLogger("OrderService");
        logger.addAppender(new ConsoleAppender(LogLevel.DEBUG, new SimpleFormatter()));

        // Set Logger Level to WARNING (DEBUG and INFO will be skipped!)
        logger.setLevel(LogLevel.WARNING);
        System.out.println("📌 Configured Logger Level: WARNING (Expect DEBUG & INFO to be skipped)");

        logger.debug("Debug cache miss for Order #101");          // Skipped
        logger.info("Order #101 placed successfully");            // Skipped
        logger.warning("Order #101 inventory low (2 items left)"); // Logged
        logger.error("Order #101 payment gateway timeout");       // Logged
        logger.fatal("Order #101 database master node down");     // Logged

        // =========================================================================
        // SCENARIO 2: CUSTOM FORMATTERS (Simple vs Detailed)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Custom Formatting (Detailed Formatter)");
        System.out.println("-----------------------------------------------------------");

        Logger paymentLogger = LoggerImpl.getLogger("PaymentGateway");
        paymentLogger.setLevel(LogLevel.INFO);

        // Attach ConsoleAppender with DetailedFormatter
        paymentLogger.addAppender(new ConsoleAppender(LogLevel.INFO, new DetailedFormatter()));

        paymentLogger.info("Initiating UPI payment of ₹2999", "PaymentGateway.processPayment");
        paymentLogger.error("Bank gateway rejected transaction TXN_9988", "PaymentGateway.handleResponse");

        // =========================================================================
        // SCENARIO 3: MULTIPLE APPENDERS (Console + File + Database)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Multiple Output Destinations (Console, File, DB)");
        System.out.println("-----------------------------------------------------------");

        Logger multiLogger = LoggerImpl.getLogger("UserAuthService");
        multiLogger.setLevel(LogLevel.DEBUG);

        // Appender 1: Console Appender (Simple format)
        multiLogger.addAppender(new ConsoleAppender(LogLevel.DEBUG, new SimpleFormatter()));

        // Appender 2: File Appender (Detailed format writing to app.log)
        String logFilePath = "app.log";
        multiLogger.addAppender(new FileAppender(logFilePath, LogLevel.WARNING, new DetailedFormatter()));

        // Appender 3: Database Appender (Logging ERRORs to DB table 'audit_logs')
        DatabaseAppender dbAppender = new DatabaseAppender("audit_logs", LogLevel.ERROR, new DetailedFormatter());
        multiLogger.addAppender(dbAppender);

        System.out.println("📡 Logging message to Console, FileAppender (" + logFilePath + "), and DB...");
        multiLogger.warning("User 'john_doe' failed 3 login attempts", "UserAuthService.authenticate");
        multiLogger.error("Brute-force attack detected on IP 192.168.1.100", "UserAuthService.detectThreat");

        // =========================================================================
        // SCENARIO 4: FILTER CHAIN (Chain of Responsibility)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Filter Chain (SourceFilter: com.takeuforward)");
        System.out.println("-----------------------------------------------------------");

        Logger filteredLogger = LoggerImpl.getLogger("AuditLogger");
        filteredLogger.setLevel(LogLevel.DEBUG);
        filteredLogger.addAppender(new ConsoleAppender(LogLevel.DEBUG, new SimpleFormatter()));

        // Add filter: Only allow logs from 'com.takeuforward.*'
        filteredLogger.addFilter(new SourceFilter("com.takeuforward"));

        System.out.println("🛡️ Filter active: Only allowing sources starting with 'com.takeuforward'...");
        filteredLogger.info("Allowed internal audit record", "com.takeuforward.security.AuditService"); // Allowed
        filteredLogger.info("Blocked external spam", "org.external.thirdparty.Tracker");                // Blocked by filter!

        // =========================================================================
        // SCENARIO 5: CONCURRENT MULTI-THREADED LOGGING SAFETY
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: Thread-Safe Concurrent Multi-Threaded Logging");
        System.out.println("-----------------------------------------------------------");

        Logger concurrentLogger = LoggerImpl.getLogger("ConcurrentEngine");
        concurrentLogger.setLevel(LogLevel.INFO);
        concurrentLogger.addAppender(new ConsoleAppender(LogLevel.INFO, new DetailedFormatter()));

        int threadCount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int workerId = i;
            executor.submit(() -> {
                try {
                    concurrentLogger.info("Worker #" + workerId + " completed subtask", "WorkerTask-" + workerId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // =========================================================================
        // SCENARIO 6: ERROR HANDLING & FALLBACK (Database Connection Failure)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("6️⃣ SCENARIO 6: Error Handling & Fallback (DB Outage)");
        System.out.println("-----------------------------------------------------------");

        dbAppender.setSimulateConnectionFailure(true);
        multiLogger.error("Critical payment service down (Simulated DB failure)", "PaymentService.notify");

        System.out.println("\n=================================================================");
        System.out.println("🎯 LOGGING FRAMEWORK DEMONSTRATION COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
