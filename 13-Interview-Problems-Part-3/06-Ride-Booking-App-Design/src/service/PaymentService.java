package service;

import domain.PaymentStatus;
import domain.strategy.PaymentGatewayProvider;
import domain.strategy.PaymentGatewayRouter;

import java.util.HashMap;

public class PaymentService {
    private final PaymentGatewayRouter gatewayRouter;

    public PaymentService(PaymentGatewayRouter gatewayRouter) {
        this.gatewayRouter = gatewayRouter;
    }

    public String initiatePayment(String rideId, long amountCents, String preferredGateway) {
        PaymentGatewayProvider provider = gatewayRouter.resolve(preferredGateway);
        return provider.initiatePayment(rideId, amountCents, new HashMap<>());
    }

    public boolean handlePaymentCallback(String transactionId, PaymentStatus status, String gatewayName) {
        PaymentGatewayProvider provider = gatewayRouter.resolve(gatewayName);
        return provider.verifyCallback(transactionId, status);
    }
}
