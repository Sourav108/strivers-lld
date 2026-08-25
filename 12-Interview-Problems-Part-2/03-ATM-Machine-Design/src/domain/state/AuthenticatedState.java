package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class AuthenticatedState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("❌ A user session is already authenticated.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("ℹ️ User is already authenticated.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        atm.setSelectedTransactionType(type);
        atm.setState(new TransactionSelectedState());
        System.out.println("📋 Transaction Selected: " + type + ". Transition -> TRANSACTION_SELECTED");
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        throw new InvalidATMOperationException("❌ Please select withdrawal transaction first.");
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        throw new InvalidATMOperationException("❌ Please select deposit transaction first.");
    }

    @Override
    public long checkBalance(ATM atm) {
        throw new InvalidATMOperationException("❌ Please select balance inquiry transaction first.");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setInsertedCardId(null);
        atm.setSelectedTransactionType(null);
        atm.setState(new IdleState());
        System.out.println("⏏️ Card ejected. Session ended. Transition -> IDLE");
    }

    @Override
    public String getStateName() {
        return "AUTHENTICATED";
    }
}
