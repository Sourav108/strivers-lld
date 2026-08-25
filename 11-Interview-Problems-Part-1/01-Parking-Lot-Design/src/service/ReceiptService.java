package service;

import domain.Receipt;

import java.util.UUID;

public class ReceiptService {
    public Receipt generateReceipt(UUID ticketId, double totalFee, Receipt.PaymentStatus status) {
        Receipt receipt = new Receipt(ticketId, totalFee, status);
        System.out.println("   🧾 Generated Receipt: " + receipt.getId() + " | Fee: ₹" + totalFee + " | Status: " + status);
        return receipt;
    }
}
