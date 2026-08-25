/**
 * Structural Design Patterns: Facade Pattern
 * 
 * Core Concept: Provides a simplified, unified interface to a complex subsystem
 * or group of classes, hiding internal orchestration details from clients.
 */

// =========================================================================
// 1. COMPLEX SUBSYSTEM CLASSES
// =========================================================================

class PaymentService {
    public void makePayment(String accountId, double amount) {
        System.out.println("💳 [PaymentService] Successfully processed ₹" + amount + " for account: " + accountId);
    }
}

class SeatReservationService {
    public void reserveSeat(String movieId, String seatNumber) {
        System.out.println("💺 [SeatReservationService] Reserved seat " + seatNumber + " for movie ID: " + movieId);
    }
}

class TicketService {
    public void generateTicket(String movieId, String seatNumber) {
        System.out.println("🎟️ [TicketService] Generated e-ticket for movie " + movieId + ", Seat: " + seatNumber);
    }
}

class LoyaltyPointsService {
    public void addPoints(String accountId, int points) {
        System.out.println("⭐ [LoyaltyPointsService] Credited " + points + " reward points to account: " + accountId);
    }
}

class NotificationService {
    public void sendBookingConfirmation(String userEmail) {
        System.out.println("📧 [NotificationService] Sent booking receipt & PDF ticket to " + userEmail);
    }
}

// =========================================================================
// 2. THE FACADE CLASS (Unified Entry Point)
// =========================================================================

class MovieBookingFacade {
    private final PaymentService paymentService;
    private final SeatReservationService seatReservationService;
    private final TicketService ticketService;
    private final LoyaltyPointsService loyaltyPointsService;
    private final NotificationService notificationService;

    public MovieBookingFacade() {
        this.paymentService = new PaymentService();
        this.seatReservationService = new SeatReservationService();
        this.ticketService = new TicketService();
        this.loyaltyPointsService = new LoyaltyPointsService();
        this.notificationService = new NotificationService();
    }

    // High-level unified API coordinating the entire multi-service workflow
    public void bookMovieTicket(String accountId, String movieId, String seatNumber, String userEmail, double amount) {
        System.out.println("--- Starting Movie Booking Flow via Facade ---");
        paymentService.makePayment(accountId, amount);
        seatReservationService.reserveSeat(movieId, seatNumber);
        ticketService.generateTicket(movieId, seatNumber);
        loyaltyPointsService.addPoints(accountId, (int) (amount * 0.10));
        notificationService.sendBookingConfirmation(userEmail);
        System.out.println("🎬 [MovieBookingFacade] Movie ticket booking completed successfully!\n");
    }
}

// =========================================================================
// 🚀 Main Driver Program
// =========================================================================
public class FacadePatternExample {
    public static void main(String[] args) {
        System.out.println("=== ❌ 1. Bad Design: Client Manually Calling Subsystems in Sequence ===");
        // Client is tightly coupled to 5 individual classes and must remember order of calls
        PaymentService payment = new PaymentService();
        payment.makePayment("user_999", 500.0);

        SeatReservationService seat = new SeatReservationService();
        seat.reserveSeat("AVATAR_3", "C14");

        TicketService ticket = new TicketService();
        ticket.generateTicket("AVATAR_3", "C14");

        LoyaltyPointsService loyalty = new LoyaltyPointsService();
        loyalty.addPoints("user_999", 50);

        NotificationService notification = new NotificationService();
        notification.sendBookingConfirmation("user999@example.com");

        System.out.println("\n=== ✅ 2. Good Design: Client Calling MovieBookingFacade ===");
        // Client interacts with a single unified, clean entry point
        MovieBookingFacade facade = new MovieBookingFacade();
        facade.bookMovieTicket("sourav_108", "INTERSTELLAR_IMAX", "H10", "sourav@example.com", 650.0);
    }
}
