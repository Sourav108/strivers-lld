package domain.strategy;

import domain.*;

import java.util.Map;
import java.util.UUID;

public class WithdrawalStrategy implements TransactionStrategy {
    @Override
    public Transaction execute(ATM atm, Account account, Session session, long amountMinorUnits, Map<Denomination, Integer> notes) {
        long amountRupees = amountMinorUnits / 100;
        String txId = "TX-WD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Transaction transaction = new Transaction(txId, atm.getId(), session.getId(), account.getId(), TransactionType.WITHDRAW, amountMinorUnits);

        try {
            // 1. Account balance & daily limit deduction
            account.withdraw(amountMinorUnits);

            // 2. ATM cash drawer note allocation & physical dispensing
            Map<Denomination, Integer> dispensed = atm.getCashDrawer().calculateAndDispense(amountRupees);
            transaction.setDispensedNotes(dispensed);
            transaction.setStatus(TransactionStatus.SUCCESS);

            atm.processWithdrawal(amountRupees); // Advances state to TransactionCompletedState
            System.out.println("💵 [Withdrawal Success] Dispensed ₹" + amountRupees + " -> Notes: " + dispensed);
            System.out.println("   Account Remaining Balance: ₹" + account.getBalanceRupees());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            System.out.println("❌ [Withdrawal Failed] " + e.getMessage());
            throw e;
        }

        return transaction;
    }
}
