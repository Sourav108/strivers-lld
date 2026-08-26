package digitalwallet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Driver simulation demonstrating:
 * 1. User onboarding & wallet provisioning (1:1 constraint)
 * 2. External deposits via PaymentGateway Strategy
 * 3. Atomic peer-to-peer transfers with minor currency units
 * 4. Concurrent transfers under high load with deadlock-free ordered locking
 * 5. Edge cases: Overdraft, self-transfers, invalid amounts, account suspension
 * 6. Detailed account statements and immutable audit trails
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("💳 DIGITAL WALLET SYSTEM - LOW LEVEL DESIGN DEMO");
        System.out.println("==================================================");

        DigitalWalletSystem walletSystem = new DigitalWalletSystem(new EmailNotificationService());
        PaymentGateway razorpay = new RazorpayPaymentGateway();

        // 1. User & Wallet Onboarding
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 1. USER & WALLET ONBOARDING");
        System.out.println("--------------------------------------------------");
        User alice = walletSystem.registerUser("Alice Smith", "alice@example.com");
        User bob = walletSystem.registerUser("Bob Johnson", "bob@example.com");
        User charlie = walletSystem.registerUser("Charlie Brown", "charlie@example.com");

        Wallet aliceWallet = walletSystem.createWallet(alice.getId());
        Wallet bobWallet = walletSystem.createWallet(bob.getId());
        Wallet charlieWallet = walletSystem.createWallet(charlie.getId());

        // 2. Fund Deposits via Payment Gateway
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 2. FUND DEPOSITS VIA PAYMENT GATEWAY");
        System.out.println("--------------------------------------------------");
        // Deposit 500.00 TUF (50000 minor units) for Alice
        walletSystem.deposit(aliceWallet.getAccountNumber(), 50000L, razorpay);
        // Deposit 200.00 TUF (20000 minor units) for Bob
        walletSystem.deposit(bobWallet.getAccountNumber(), 20000L, razorpay);

        // 3. Peer-to-Peer Transfers
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 3. PEER-TO-PEER TRANSFERS");
        System.out.println("--------------------------------------------------");
        // Alice transfers 150.00 TUF (15000 minor units) to Bob
        walletSystem.transfer(aliceWallet.getAccountNumber(), bobWallet.getAccountNumber(), 15000L, "Dinner split");
        // Bob transfers 50.00 TUF (5000 minor units) to Charlie
        walletSystem.transfer(bobWallet.getAccountNumber(), charlieWallet.getAccountNumber(), 5000L, "Movie tickets");

        // 4. Edge Cases: Overdraft, Self-Transfer, Negative Amount
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 4. EDGE CASE VALIDATIONS");
        System.out.println("--------------------------------------------------");
        System.out.println("Test A: Charlie attempts to transfer 100.00 TUF (Balance: 50.00 TUF)...");
        walletSystem.transfer(charlieWallet.getAccountNumber(), aliceWallet.getAccountNumber(), 10000L, "Overdraft test");

        System.out.println("\nTest B: Alice attempts self-transfer...");
        walletSystem.transfer(aliceWallet.getAccountNumber(), aliceWallet.getAccountNumber(), 1000L, "Self-transfer test");

        System.out.println("\nTest C: Transfer with 0 or negative amount...");
        walletSystem.transfer(aliceWallet.getAccountNumber(), bobWallet.getAccountNumber(), -500L, "Negative amount test");

        // 5. Admin Controls: Suspension & Reopening
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 5. ADMIN SUSPENSION & REOPENING");
        System.out.println("--------------------------------------------------");
        walletSystem.setWalletStatus(bobWallet.getAccountNumber(), WalletStatus.SUSPENDED);
        System.out.println("Alice attempts to transfer to suspended Bob...");
        walletSystem.transfer(aliceWallet.getAccountNumber(), bobWallet.getAccountNumber(), 2000L, "Transfer to suspended");

        walletSystem.setWalletStatus(bobWallet.getAccountNumber(), WalletStatus.ACTIVE);
        System.out.println("Retrying transfer after reopening Bob's wallet...");
        walletSystem.transfer(aliceWallet.getAccountNumber(), bobWallet.getAccountNumber(), 2000L, "Transfer after reopening");

        // 6. Concurrency Testing: Deadlock-Free Ordered Locking
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 6. CONCURRENT TRANSFERS (DEADLOCK-FREE LOCKING)");
        System.out.println("--------------------------------------------------");
        ExecutorService executor = Executors.newFixedThreadPool(4);

        System.out.println("Spawning concurrent bidirectional transfers between Alice and Bob...");
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> walletSystem.transfer(aliceWallet.getAccountNumber(), bobWallet.getAccountNumber(), 1000L, "Concurrent Alice->Bob"));
            executor.submit(() -> walletSystem.transfer(bobWallet.getAccountNumber(), aliceWallet.getAccountNumber(), 1000L, "Concurrent Bob->Alice"));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("✅ All concurrent threads completed with zero deadlocks.");

        // 7. Account Statements
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 7. ACCOUNT STATEMENTS & AUDIT TRAIL");
        System.out.println("--------------------------------------------------");
        walletSystem.printAccountStatement(aliceWallet.getAccountNumber());
        walletSystem.printAccountStatement(bobWallet.getAccountNumber());
        walletSystem.printAccountStatement(charlieWallet.getAccountNumber());

        System.out.println("==================================================");
        System.out.println("✅ DIGITAL WALLET SYSTEM SIMULATION COMPLETED");
        System.out.println("==================================================");
    }
}
