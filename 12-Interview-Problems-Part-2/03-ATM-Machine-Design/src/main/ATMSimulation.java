package main;

import controller.*;
import domain.*;
import domain.exception.CardBlockedException;
import domain.exception.InsufficientFundsException;
import domain.exception.InvalidATMOperationException;
import repository.*;
import repository.impl.*;
import service.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * ATMSimulation: Complete End-to-End Simulation of the Automated Teller Machine (ATM)
 * 
 * Demonstrates:
 * 1. Admin Refill & Cash Inventory Management (Denominations: 500, 200, 100)
 * 2. State Pattern Lifecycle (IDLE -> CARD_INSERTED -> AUTHENTICATED -> TRANSACTION_SELECTED -> TRANSACTION_COMPLETED -> IDLE)
 * 3. Strategy Pattern: Withdrawal (Greedy Note Dispensation), Deposit, and Balance Inquiry
 * 4. Security & Safety: 3 PIN retry limits with automatic Card Blocking
 * 5. Multi-Layer Validations: Account Balance, Daily Withdrawal Limit, and ATM Drawer Inventory
 * 6. Admin Management: Taking ATM In / Out of Service
 */

public class ATMSimulation {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🏧 AUTOMATED TELLER MACHINE (ATM) - LLD INTERVIEW DEMONSTRATION");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        ATMRepository atmRepo = new ATMRepositoryImpl();
        AccountRepository accountRepo = new AccountRepositoryImpl();
        CardRepository cardRepo = new CardRepositoryImpl();
        CashDrawerRepository drawerRepo = new CashDrawerRepositoryImpl();
        SessionRepository sessionRepo = new SessionRepositoryImpl();
        TransactionRepository transactionRepo = new TransactionRepositoryImpl();
        AdminUserRepository adminRepo = new AdminUserRepositoryImpl();

        // --- 2. INITIALIZE SERVICES ---
        ATMService atmService = new ATMService(atmRepo, drawerRepo);
        AdminService adminService = new AdminService(adminRepo, drawerRepo);
        CardService cardService = new CardService(cardRepo);
        SessionService sessionService = new SessionService(sessionRepo, atmRepo, cardRepo);
        TransactionService transactionService = new TransactionService(sessionRepo, accountRepo, atmRepo, transactionRepo);

        // --- 3. INITIALIZE CONTROLLERS ---
        ATMController atmController = new ATMController(atmService);
        AdminController adminController = new AdminController(adminService);
        CardController cardController = new CardController(cardService);
        SessionController sessionController = new SessionController(sessionService);
        TransactionController transactionController = new TransactionController(transactionService);

        // --- 4. SEED ADMIN & REFILL ATM ---
        AdminUser admin = new AdminUser("ADMIN-01", "Chief Custodian Raj", "9999");
        adminRepo.save(admin);

        ATM atm = atmController.createATM("ATM-KORAMANGALA-01", "Koramangala 5th Block, Bengaluru");

        Map<Denomination, Integer> initialRefill = new EnumMap<>(Denomination.class);
        initialRefill.put(Denomination.FIVE_HUNDRED, 20); // ₹10,000
        initialRefill.put(Denomination.TWO_HUNDRED, 20);  // ₹4,000
        initialRefill.put(Denomination.ONE_HUNDRED, 50);  // ₹5,000
        adminController.refillCash(atm.getId(), initialRefill); // Total: ₹19,000

        // --- 5. SEED USER ACCOUNT & CARD ---
        Account aliceAccount = new Account("ACC-101", "Alice Sharma", 50000_00L, 20000_00L); // Bal: ₹50,000, Limit: ₹20,000
        accountRepo.save(aliceAccount);

        Card aliceCard = new Card("CARD-ALICE-1234", aliceAccount.getId(), "12/28", "1234");
        cardController.registerCard(aliceCard);

        Account bobAccount = new Account("ACC-102", "Bob Verma", 2000_00L, 10000_00L); // Bal: ₹2,000
        accountRepo.save(bobAccount);

        Card bobCard = new Card("CARD-BOB-5678", bobAccount.getId(), "08/27", "4321");
        cardController.registerCard(bobCard);

        // =========================================================================
        // SCENARIO 1: HAPPY PATH WITHDRAWAL (State Pattern + Strategy Pattern)
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Alice Withdraws Cash (₹2,800)");
        System.out.println("-----------------------------------------------------------");

        // 1. Insert Card
        cardController.insertCard(atm, aliceCard.getId());
        Session session1 = sessionController.startSession(atm.getId(), aliceCard.getId());

        // 2. Authenticate with PIN
        cardController.authenticateCard(atm, aliceCard.getId(), "1234");

        // 3. Check Balance
        transactionController.showBalance(session1.getId());

        // 4. Withdraw Cash ₹2,800 (Should dispense: 5x500=2500, 1x200=200, 1x100=100)
        Transaction tx1 = transactionController.withdrawCash(session1.getId(), 2800_00L);
        transactionController.acknowledgeTransaction(tx1.getId());

        // 5. End session & eject card
        sessionController.endSession(session1.getId());
        System.out.println("ATM State after ejection: " + atm.getCurrentState().getStateName());

        // =========================================================================
        // SCENARIO 2: CASH DEPOSIT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Alice Deposits Cash (₹1,500)");
        System.out.println("-----------------------------------------------------------");

        cardController.insertCard(atm, aliceCard.getId());
        Session session2 = sessionController.startSession(atm.getId(), aliceCard.getId());
        cardController.authenticateCard(atm, aliceCard.getId(), "1234");

        Map<Denomination, Integer> depositNotes = new EnumMap<>(Denomination.class);
        depositNotes.put(Denomination.FIVE_HUNDRED, 2); // ₹1,000
        depositNotes.put(Denomination.ONE_HUNDRED, 5);  // ₹500
        Transaction tx2 = transactionController.depositCash(session2.getId(), depositNotes);
        transactionController.acknowledgeTransaction(tx2.getId());

        sessionController.endSession(session2.getId());

        // =========================================================================
        // SCENARIO 3: WRONG PIN & CARD BLOCKING TRAP
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Incorrect PIN Attempts & Card Blocking");
        System.out.println("-----------------------------------------------------------");

        cardController.insertCard(atm, bobCard.getId());
        Session session3 = sessionController.startSession(atm.getId(), bobCard.getId());

        // Attempt 1: Wrong PIN
        cardController.authenticateCard(atm, bobCard.getId(), "0000");

        // Attempt 2: Wrong PIN
        cardController.authenticateCard(atm, bobCard.getId(), "1111");

        // Attempt 3: Wrong PIN -> Should BLOCK card!
        try {
            cardController.authenticateCard(atm, bobCard.getId(), "2222");
        } catch (CardBlockedException e) {
            System.out.println("   🛡️ Caught Expected Security Violation -> " + e.getMessage());
        }

        sessionController.endSession(session3.getId());

        // =========================================================================
        // SCENARIO 4: INSUFFICIENT ACCOUNT BALANCE TRAP
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Insufficient Balance Rejection");
        System.out.println("-----------------------------------------------------------");

        Card charlieCard = new Card("CARD-CHARLIE-9999", bobAccount.getId(), "05/29", "5555");
        cardController.registerCard(charlieCard);

        cardController.insertCard(atm, charlieCard.getId());
        Session session4 = sessionController.startSession(atm.getId(), charlieCard.getId());
        cardController.authenticateCard(atm, charlieCard.getId(), "5555");

        try {
            System.out.println("⚠️ Attempting to withdraw ₹5,000 from account with only ₹2,000...");
            transactionController.withdrawCash(session4.getId(), 5000_00L);
        } catch (InsufficientFundsException e) {
            System.out.println("   🛡️ Caught Expected Balance Violation -> " + e.getMessage());
        }

        sessionController.endSession(session4.getId());

        // =========================================================================
        // SCENARIO 5: ADMIN TAKES ATM OUT OF SERVICE
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: Admin Takes ATM Out Of Service");
        System.out.println("-----------------------------------------------------------");

        atmController.takeOffline(atm.getId());

        try {
            System.out.println("⚠️ Customer attempting to insert card while ATM is Out Of Service...");
            cardController.insertCard(atm, aliceCard.getId());
        } catch (InvalidATMOperationException e) {
            System.out.println("   🛡️ Caught Expected Hardware State Violation -> " + e.getMessage());
        }

        // Restore ATM online
        atmController.bringOnline(atm.getId());
        adminController.auditCash(atm.getId());

        System.out.println("\n=================================================================");
        System.out.println("🎯 ATM MACHINE DESIGN ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
