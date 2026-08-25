package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class TransactionCompletedState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("❌ Current transaction finished. Please eject card first.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("❌ Transaction already completed.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        // User can perform another transaction in same session
        atm.setSelectedTransactionType(type);
        atm.setState(new TransactionSelectedState());
        System.out.println("📋 Next Transaction Selected: " + type + ". Transition -> TRANSACTION_SELECTED");
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        throw new InvalidATMOperationException("❌ Transaction already completed.");
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        throw new InvalidATMOperationException("❌ Transaction already completed.");
    }

    @Override
    public long checkBalance(ATM atm) {
        throw new InvalidATMOperationException("❌ Transaction already completed.");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setInsertedCardId(null);
        atm.setSelectedTransactionType(null);
        atm.setState(new IdleState());
        System.out.println("⏏️ Card ejected. Thank you for using our ATM! Transition -> IDLE");
    }

    @Override
    public String getStateName() {
        return "TRANSACTION_COMPLETED";
    }
}
