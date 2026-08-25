package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        atm.setInsertedCardId(cardId);
        atm.setState(new CardInsertedState());
        System.out.println("💳 Card #" + cardId + " inserted. Transition -> CARD_INSERTED");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("❌ Please insert your card first.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        throw new InvalidATMOperationException("❌ Please insert your card and authenticate first.");
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        throw new InvalidATMOperationException("❌ No active transaction in IDLE state.");
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        throw new InvalidATMOperationException("❌ No active transaction in IDLE state.");
    }

    @Override
    public long checkBalance(ATM atm) {
        throw new InvalidATMOperationException("❌ Please insert your card first.");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("ℹ️ No card to eject in IDLE state.");
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
