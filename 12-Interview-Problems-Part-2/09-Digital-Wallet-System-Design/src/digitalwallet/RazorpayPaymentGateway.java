package digitalwallet;

/**
 * Concrete payment gateway implementation simulating Razorpay.
 */
public class RazorpayPaymentGateway implements PaymentGateway {

    @Override
    public String getName() {
        return "Razorpay";
    }

    @Override
    public boolean processPayment(String accountNumber, long amountMinor) {
        System.out.println("💳 [Razorpay] Initiating payment of " +
                String.format("%.2f TUF", amountMinor / 100.0) + " for Account " + accountNumber + "...");
        // Simulated successful gateway callback
        System.out.println("✅ [Razorpay] Payment captured successfully.");
        return true;
    }
}
