package adapter;

public class RazorpayAdapter implements PaymentGatewayAdapter {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("   💳 [Razorpay Gateway] Successfully processed payment of ₹" + amount);
        return true;
    }
}
