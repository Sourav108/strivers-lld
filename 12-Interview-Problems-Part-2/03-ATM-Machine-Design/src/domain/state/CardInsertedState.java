package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class CardInsertedState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("❌ Card already inserted. Please authenticate or eject.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        // Validation will be coordinated by Service / Card authentication
        atm.setState(new AuthenticatedState());
        System.out.println("🔐 PIN Verified successfully. Transition -> AUTHENTICATED");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        throw new InvalidATMOperationException("❌ Please enter your PIN first.");
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        throw new InvalidATMOperationException("❌ Please authenticate first.");
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        throw new InvalidATMOperationException("❌ Please authenticate first.");
    }

    @Override
    public long checkBalance(ATM atm) {
        throw new InvalidATMOperationException("❌ Please enter your PIN first.");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setInsertedCardId(null);
        atm.setState(new IdleState());
        System.out.println("⏏️ Card ejected. Transition -> IDLE");
    }

    @Override
    public String getStateName() {
        return "CARD_INSERTED";
    }
}
