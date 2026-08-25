package adapter;

public interface PaymentGatewayAdapter {
    boolean processPayment(double amount);
}
