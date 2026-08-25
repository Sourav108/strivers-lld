package repository;

import domain.Payment;
import domain.Receipt;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentRepository {
    private final Map<UUID, Payment> payments = new ConcurrentHashMap<>();

    public Payment save(Payment payment) {
        payments.put(payment.getId(), payment);
        return payment;
    }

    public Optional<Payment> findById(UUID paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public void updateStatus(UUID paymentId, Receipt.PaymentStatus status) {
        Payment payment = payments.get(paymentId);
        if (payment != null) {
            payment.setStatus(status);
        }
    }
}
