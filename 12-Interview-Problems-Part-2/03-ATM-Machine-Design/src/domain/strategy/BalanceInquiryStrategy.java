package domain.strategy;

import domain.*;

import java.util.Map;
import java.util.UUID;

public class BalanceInquiryStrategy implements TransactionStrategy {
    @Override
    public Transaction execute(ATM atm, Account account, Session session, long amountMinorUnits, Map<Denomination, Integer> notes) {
        String txId = "TX-BAL-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Transaction transaction = new Transaction(txId, atm.getId(), session.getId(), account.getId(), TransactionType.BALANCE, account.getBalanceMinorUnits());
        transaction.setStatus(TransactionStatus.SUCCESS);

        atm.checkBalance(); // Advances state to TransactionCompletedState
        System.out.println("💰 [Balance Inquiry] Account Holder: " + account.getHolderName() + " | Balance: ₹" + account.getBalanceRupees());

        return transaction;
    }
}
