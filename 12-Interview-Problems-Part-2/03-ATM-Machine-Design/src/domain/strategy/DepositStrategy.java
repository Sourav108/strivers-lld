package domain.strategy;

import domain.*;

import java.util.Map;
import java.util.UUID;

public class DepositStrategy implements TransactionStrategy {
    @Override
    public Transaction execute(ATM atm, Account account, Session session, long amountMinorUnits, Map<Denomination, Integer> notes) {
        if (notes == null || notes.isEmpty()) {
            throw new IllegalArgumentException("No cash notes provided for deposit.");
        }

        long calculatedRupees = 0;
        for (Map.Entry<Denomination, Integer> entry : notes.entrySet()) {
            calculatedRupees += (long) entry.getKey().getValue() * entry.getValue();
        }
        long calculatedMinor = calculatedRupees * 100;

        String txId = "TX-DEP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Transaction transaction = new Transaction(txId, atm.getId(), session.getId(), account.getId(), TransactionType.DEPOSIT, calculatedMinor);
        transaction.setDepositedNotes(notes);

        // 1. Add cash to ATM drawer
        atm.getCashDrawer().deposit(notes);

        // 2. Credit account balance
        account.deposit(calculatedMinor);
        transaction.setStatus(TransactionStatus.SUCCESS);

        atm.processDeposit(notes); // Advances state to TransactionCompletedState
        System.out.println("📥 [Deposit Success] Deposited ₹" + calculatedRupees + " -> Notes: " + notes);
        System.out.println("   Account New Balance: ₹" + account.getBalanceRupees());

        return transaction;
    }
}
