package domain.state;

import domain.ATM;
import domain.Denomination;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

import java.util.Map;

public interface ATMState {
    void insertCard(ATM atm, String cardId) throws InvalidATMOperationException;
    void enterPin(ATM atm, String pin) throws InvalidATMOperationException;
    void selectTransaction(ATM atm, TransactionType type) throws InvalidATMOperationException;
    void processWithdrawal(ATM atm, long amountRupees) throws InvalidATMOperationException;
    void processDeposit(ATM atm, Map<Denomination, Integer> notes) throws InvalidATMOperationException;
    long checkBalance(ATM atm) throws InvalidATMOperationException;
    void ejectCard(ATM atm) throws InvalidATMOperationException;
    String getStateName();
}
