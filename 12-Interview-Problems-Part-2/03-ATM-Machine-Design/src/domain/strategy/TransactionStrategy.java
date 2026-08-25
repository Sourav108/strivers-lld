package domain.strategy;

import domain.ATM;
import domain.Account;
import domain.Denomination;
import domain.Session;
import domain.Transaction;

import java.util.Map;

public interface TransactionStrategy {
    Transaction execute(ATM atm, Account account, Session session, long amountMinorUnits, Map<Denomination, Integer> notes);
}
