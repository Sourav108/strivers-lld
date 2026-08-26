package controller;

import domain.PaymentStatus;
import service.PaymentService;

public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public boolean handlePaymentCallback(String transactionId, PaymentStatus status, String gatewayName) {
        return paymentService.handlePaymentCallback(transactionId, status, gatewayName);
    }
}
