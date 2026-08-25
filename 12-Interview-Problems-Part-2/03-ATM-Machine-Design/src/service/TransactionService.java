package service;

import domain.*;
import domain.strategy.BalanceInquiryStrategy;
import domain.strategy.DepositStrategy;
import domain.strategy.TransactionStrategy;
import domain.strategy.WithdrawalStrategy;
import repository.*;

import java.util.Map;

public class TransactionService {
    private final SessionRepository sessionRepository;
    private final AccountRepository accountRepository;
    private final ATMRepository atmRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(SessionRepository sessionRepository,
                              AccountRepository accountRepository,
                              ATMRepository atmRepository,
                              TransactionRepository transactionRepository) {
        this.sessionRepository = sessionRepository;
        this.accountRepository = accountRepository;
        this.atmRepository = atmRepository;
        this.transactionRepository = transactionRepository;
    }

    private Session getActiveSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session #" + sessionId + " not found."));
        if (!session.isActive()) {
            throw new IllegalStateException("Session #" + sessionId + " is already closed.");
        }
        return session;
    }

    public synchronized Transaction withdrawCash(String sessionId, long amountMinorUnits) {
        Session session = getActiveSession(sessionId);
        ATM atm = atmRepository.findById(session.getAtmId()).orElseThrow();
        Account account = accountRepository.findById(session.getAccountId()).orElseThrow();

        // 1. Select transaction on ATM state
        atm.selectTransaction(TransactionType.WITHDRAW);

        // 2. Execute withdrawal strategy
        TransactionStrategy strategy = new WithdrawalStrategy();
        Transaction transaction = strategy.execute(atm, account, session, amountMinorUnits, null);

        // 3. Persist transaction & updated balances
        transactionRepository.save(transaction);
        accountRepository.save(account);
        session.setCurrentTransactionId(transaction.getId());

        return transaction;
    }

    public synchronized Transaction depositCash(String sessionId, Map<Denomination, Integer> notes) {
        Session session = getActiveSession(sessionId);
        ATM atm = atmRepository.findById(session.getAtmId()).orElseThrow();
        Account account = accountRepository.findById(session.getAccountId()).orElseThrow();

        // 1. Select transaction on ATM state
        atm.selectTransaction(TransactionType.DEPOSIT);

        // 2. Execute deposit strategy
        TransactionStrategy strategy = new DepositStrategy();
        Transaction transaction = strategy.execute(atm, account, session, 0, notes);

        // 3. Persist transaction & updated balances
        transactionRepository.save(transaction);
        accountRepository.save(account);
        session.setCurrentTransactionId(transaction.getId());

        return transaction;
    }

    public synchronized Transaction showBalance(String sessionId) {
        Session session = getActiveSession(sessionId);
        ATM atm = atmRepository.findById(session.getAtmId()).orElseThrow();
        Account account = accountRepository.findById(session.getAccountId()).orElseThrow();

        // 1. Select transaction on ATM state
        atm.selectTransaction(TransactionType.BALANCE);

        // 2. Execute balance inquiry strategy
        TransactionStrategy strategy = new BalanceInquiryStrategy();
        Transaction transaction = strategy.execute(atm, account, session, 0, null);

        // 3. Persist transaction
        transactionRepository.save(transaction);
        session.setCurrentTransactionId(transaction.getId());

        return transaction;
    }

    public void acknowledgeTransaction(String transactionId) {
        transactionRepository.findById(transactionId).ifPresent(tx -> {
            System.out.println("🧾 [Receipt Printed] Transaction #" + transactionId + " acknowledged. Status: " + tx.getStatus());
        });
    }
}
