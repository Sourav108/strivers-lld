package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class OutOfServiceState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE. Please try another ATM.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE.");
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE.");
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE.");
    }

    @Override
    public long checkBalance(ATM atm) {
        throw new InvalidATMOperationException("🚫 ATM is currently OUT OF SERVICE.");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setInsertedCardId(null);
        System.out.println("⏏️ Card ejected from Out Of Service ATM.");
    }

    @Override
    public String getStateName() {
        return "OUT_OF_SERVICE";
    }
}
