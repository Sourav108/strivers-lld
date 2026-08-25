package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public class TransactionSelectedState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("❌ Transaction currently in progress.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("❌ Already authenticated.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        atm.setSelectedTransactionType(type);
        System.out.println("📋 Transaction changed to: " + type);
    }

    @Override
    public void processWithdrawal(ATM atm, long amountRupees) {
        if (atm.getSelectedTransactionType() != TransactionType.WITHDRAW) {
            throw new InvalidATMOperationException("❌ Selected transaction type is not WITHDRAW.");
        }
        atm.setState(new TransactionCompletedState());
    }

    @Override
    public void processDeposit(ATM atm, Map<Denomination, Integer> notes) {
        if (atm.getSelectedTransactionType() != TransactionType.DEPOSIT) {
            throw new InvalidATMOperationException("❌ Selected transaction type is not DEPOSIT.");
        }
        atm.setState(new TransactionCompletedState());
    }

    @Override
    public long checkBalance(ATM atm) {
        if (atm.getSelectedTransactionType() != TransactionType.BALANCE) {
            throw new InvalidATMOperationException("❌ Selected transaction type is not BALANCE.");
        }
        atm.setState(new TransactionCompletedState());
        return 0;
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setInsertedCardId(null);
        atm.setSelectedTransactionType(null);
        atm.setState(new IdleState());
        System.out.println("⏏️ Transaction cancelled. Card ejected. Transition -> IDLE");
    }

    @Override
    public String getStateName() {
        return "TRANSACTION_SELECTED";
    }
}
