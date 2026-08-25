package domain;

import java.util.UUID;

public class Payment {
    public enum PaymentGateway {
        RAZORPAY,
        STRIPE
    }

    private final UUID id;
    private final UUID ticketId;
    private final double amount;
    private final PaymentGateway gateway;
    private Receipt.PaymentStatus status;

    public Payment(UUID ticketId, double amount, PaymentGateway gateway) {
        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.amount = amount;
        this.gateway = gateway;
        this.status = Receipt.PaymentStatus.PENDING;
    }

    public void setStatus(Receipt.PaymentStatus status) { this.status = status; }

    public UUID getId() { return id; }
    public UUID getTicketId() { return ticketId; }
    public double getAmount() { return amount; }
    public PaymentGateway getGateway() { return gateway; }
    public Receipt.PaymentStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "Payment{" + "id=" + id + ", ticketId=" + ticketId + ", amount=" + amount + ", gateway=" + gateway + ", status=" + status + '}';
    }
}
