package domain.strategy;

import domain.PaymentStatus;

import java.util.Map;
import java.util.UUID;

public class MockPaymentGatewayProvider implements PaymentGatewayProvider {

    @Override
    public String getName() {
        return "MOCK";
    }

    @Override
    public String initiatePayment(String rideId, long amountCents, Map<String, String> paymentDetails) {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public boolean verifyCallback(String transactionId, PaymentStatus status) {
        return status == PaymentStatus.COMPLETED;
    }
}
