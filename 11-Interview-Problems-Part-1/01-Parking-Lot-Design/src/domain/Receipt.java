package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Receipt {
    public enum PaymentStatus {
        SUCCESS,
        FAILED,
        PENDING
    }

    private final UUID id;
    private final UUID ticketId;
    private final LocalDateTime exitTime;
    private final double totalFee;
    private final PaymentStatus paymentStatus;

    public Receipt(UUID ticketId, double totalFee, PaymentStatus paymentStatus) {
        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.exitTime = LocalDateTime.now();
        this.totalFee = totalFee;
        this.paymentStatus = paymentStatus;
    }

    public UUID getId() { return id; }
    public UUID getTicketId() { return ticketId; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getTotalFee() { return totalFee; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    @Override
    public String toString() {
        return "Receipt{" + "id=" + id + ", ticketId=" + ticketId + ", exitTime=" + exitTime + ", totalFee=" + totalFee + ", status=" + paymentStatus + '}';
    }
}
