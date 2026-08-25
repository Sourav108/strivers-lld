package controller;

import domain.Booking;
import domain.Transaction;
import domain.TransactionStatus;
import service.TransactionService;

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction initiateTransaction(Booking booking) {
        return transactionService.initiateTransaction(booking);
    }

    public void handleTransactionCallback(String providerRef, TransactionStatus status) {
        transactionService.handleCallback(providerRef, status);
    }
}
