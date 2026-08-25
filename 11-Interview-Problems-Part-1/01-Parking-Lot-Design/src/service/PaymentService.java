package service;

import adapter.PaymentGatewayAdapter;
import domain.Payment;
import domain.Receipt;
import repository.PaymentRepository;

import java.util.UUID;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(UUID ticketId, double amount, PaymentGatewayAdapter gatewayAdapter, Payment.PaymentGateway gatewayType) {
        Payment payment = new Payment(ticketId, amount, gatewayType);
        paymentRepository.save(payment);

        int maxRetries = 2;
        boolean success = false;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            System.out.println("   💳 [Payment Attempt #" + attempt + "] Processing ₹" + amount + " via " + gatewayType + "...");
            success = gatewayAdapter.processPayment(amount);
            if (success) {
                payment.setStatus(Receipt.PaymentStatus.SUCCESS);
                paymentRepository.updateStatus(payment.getId(), Receipt.PaymentStatus.SUCCESS);
                break;
            } else {
                System.out.println("   ⚠️ Payment failed on attempt #" + attempt + ". Retrying...");
            }
        }

        if (!success) {
            payment.setStatus(Receipt.PaymentStatus.FAILED);
            paymentRepository.updateStatus(payment.getId(), Receipt.PaymentStatus.FAILED);
        }

        return payment;
    }
}
