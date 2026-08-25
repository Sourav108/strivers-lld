package controller;

import adapter.PaymentGatewayAdapter;
import domain.Payment;
import domain.Receipt;
import domain.Ticket;
import domain.Vehicle;
import service.*;

import java.util.Optional;
import java.util.UUID;

public class ExitController {
    public static class ExitResult {
        private final boolean success;
        private final UUID receiptId;
        private final double amountPaid;
        private final String message;

        public ExitResult(boolean success, UUID receiptId, double amountPaid, String message) {
            this.success = success;
            this.receiptId = receiptId;
            this.amountPaid = amountPaid;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public UUID getReceiptId() { return receiptId; }
        public double getAmountPaid() { return amountPaid; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "ExitResult{" + "success=" + success + ", receiptId=" + receiptId + ", amountPaid=" + amountPaid + ", message='" + message + '\'' + '}';
        }
    }

    private final TicketService ticketService;
    private final SlotService slotService;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final ReceiptService receiptService;

    public ExitController(TicketService ticketService, SlotService slotService, PricingService pricingService,
                          PaymentService paymentService, ReceiptService receiptService) {
        this.ticketService = ticketService;
        this.slotService = slotService;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.receiptService = receiptService;
    }

    public ExitResult exitVehicle(UUID ticketId, Vehicle.VehicleType vehicleType, long simulatedDurationHours,
                                  PaymentGatewayAdapter gatewayAdapter, Payment.PaymentGateway gatewayType) {
        System.out.println("\n🏁 [Exit Gate] Processing exit for Ticket ID: " + ticketId);

        Optional<Ticket> ticketOpt = ticketService.getTicket(ticketId);
        if (ticketOpt.isEmpty() || !ticketOpt.get().isActive()) {
            System.out.println("   ❌ [Exit Denied] Invalid, inactive, or already processed ticket.");
            return new ExitResult(false, null, 0.0, "Invalid or expired ticket ID.");
        }

        Ticket ticket = ticketOpt.get();

        // 1. Calculate Fee
        double totalFee = pricingService.calculateFee(vehicleType, simulatedDurationHours);
        System.out.println("   💲 [Fee Calculated] Duration: " + simulatedDurationHours + " hrs | Total: ₹" + totalFee);

        // 2. Process Payment
        Payment payment = paymentService.processPayment(ticketId, totalFee, gatewayAdapter, gatewayType);
        if (payment.getStatus() != Receipt.PaymentStatus.SUCCESS) {
            System.out.println("   ❌ [Exit Blocked] Payment was not successful. Barrier stays closed.");
            return new ExitResult(false, null, totalFee, "Payment failed. Please retry.");
        }

        // 3. Release Slot
        slotService.releaseSlot(ticket.getSlotId());
        System.out.println("   🔓 [Slot Released] Slot #" + ticket.getSlotId() + " is now free.");

        // 4. Deactivate Ticket
        ticketService.deactivateTicket(ticketId);

        // 5. Generate Receipt
        Receipt receipt = receiptService.generateReceipt(ticketId, totalFee, Receipt.PaymentStatus.SUCCESS);
        System.out.println("   ✅ [Exit Approved] Barrier opened! Have a safe journey.");

        return new ExitResult(true, receipt.getId(), totalFee, "Exit processed successfully");
    }
}
