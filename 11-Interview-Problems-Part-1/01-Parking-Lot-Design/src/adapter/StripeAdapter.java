package adapter;

public class StripeAdapter implements PaymentGatewayAdapter {
    private boolean simulateFailure = false;

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public boolean processPayment(double amount) {
        if (simulateFailure) {
            System.out.println("   ❌ [Stripe Gateway] Payment failed (Card network unavailable).");
            return false;
        }
        System.out.println("   💳 [Stripe Gateway] Successfully processed payment of ₹" + amount);
        return true;
    }
}
