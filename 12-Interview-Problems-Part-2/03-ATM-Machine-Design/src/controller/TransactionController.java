package controller;

import domain.Denomination;
import domain.Transaction;
import service.TransactionService;

import java.util.Map;

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction showBalance(String sessionId) {
        return transactionService.showBalance(sessionId);
    }

    public Transaction withdrawCash(String sessionId, long amountMinorUnits) {
        return transactionService.withdrawCash(sessionId, amountMinorUnits);
    }

    public Transaction depositCash(String sessionId, Map<Denomination, Integer> notes) {
        return transactionService.depositCash(sessionId, notes);
    }

    public void acknowledgeTransaction(String transactionId) {
        transactionService.acknowledgeTransaction(transactionId);
    }
}
