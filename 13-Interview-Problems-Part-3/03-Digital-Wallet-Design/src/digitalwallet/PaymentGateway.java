package digitalwallet;

/**
 * Strategy interface for external payment gateway integrations (Strategy Pattern).
 */
public interface PaymentGateway {
    /**
     * Returns the name of the payment provider.
     */
    String getName();

    /**
     * Simulates charging an external funding source to deposit into a wallet.
     *
     * @param accountNumber the destination wallet account
     * @param amountMinor   the amount in minor units
     * @return true if payment succeeded, false otherwise
     */
    boolean processPayment(String accountNumber, long amountMinor);
}
