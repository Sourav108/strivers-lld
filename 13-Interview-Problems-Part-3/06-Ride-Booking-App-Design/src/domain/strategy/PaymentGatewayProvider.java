package domain.strategy;

import domain.PaymentStatus;

import java.util.Map;

public interface PaymentGatewayProvider {
    String getName();
    String initiatePayment(String rideId, long amountCents, Map<String, String> paymentDetails);
    boolean verifyCallback(String transactionId, PaymentStatus status);
}
