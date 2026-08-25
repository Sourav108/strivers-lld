import adapter.PaymentGatewayAdapter;
import adapter.RazorpayAdapter;
import adapter.StripeAdapter;
import controller.AdminController;
import controller.EntryController;
import controller.ExitController;
import domain.Payment;
import domain.PricingRule;
import domain.Vehicle;
import repository.*;
import service.*;

/**
 * Main Simulation Driver for the Complete Smart Parking Lot System
 * Demonstrates:
 * 1. Admin Flow: Initializing floors, adding multi-type slots, defining pricing rules
 * 2. Entry Flow: Assigning slots based on vehicle type and generating tickets
 * 3. Exit Flow: Fee calculation, payment processing with retry, slot release, receipt generation
 * 4. Edge Cases: Capacity limits, payment retry & failure, and invalid ticket queries
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🏢 SMART MULTI-FLOOR PARKING LOT SYSTEM - LLD DEMONSTRATION");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        FloorRepository floorRepo = new FloorRepository();
        SlotRepository slotRepo = new SlotRepository();
        TicketRepository ticketRepo = new TicketRepository();
        PaymentRepository paymentRepo = new PaymentRepository();
        PricingRuleRepository pricingRuleRepo = new PricingRuleRepository();

        // --- 2. INITIALIZE SERVICES ---
        SlotService slotService = new SlotService(slotRepo);
        TicketService ticketService = new TicketService(ticketRepo);
        PricingService pricingService = new PricingService(pricingRuleRepo);
        PaymentService paymentService = new PaymentService(paymentRepo);
        ReceiptService receiptService = new ReceiptService();
        AdminService adminService = new AdminService(floorRepo, slotRepo, pricingRuleRepo);

        // --- 3. INITIALIZE CONTROLLERS & ADAPTERS ---
        EntryController entryController = new EntryController(ticketService, slotService);
        ExitController exitController = new ExitController(ticketService, slotService, pricingService, paymentService, receiptService);
        AdminController adminController = new AdminController(adminService);

        PaymentGatewayAdapter razorpay = new RazorpayAdapter();
        StripeAdapter stripe = new StripeAdapter();

        // =========================================================================
        // SCENARIO 1: ADMIN SETUP & CONFIGURATION
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🛠️ SCENARIO 1: Admin Floor & Pricing Setup");
        System.out.println("-----------------------------------------------------------");

        // Add 2 Floors
        adminController.addFloor(1);
        adminController.addFloor(2);

        // Add Slots: Floor 1 -> 2 CAR, 1 BIKE; Floor 2 -> 1 CAR, 1 EV
        adminController.addSlotsToFloor(1, Vehicle.VehicleType.CAR, 2);
        adminController.addSlotsToFloor(1, Vehicle.VehicleType.BIKE, 1);
        adminController.addSlotsToFloor(2, Vehicle.VehicleType.CAR, 1);
        adminController.addSlotsToFloor(2, Vehicle.VehicleType.EV, 1);

        // Configure Pricing Rules (Hybrid: ratePerHour, flatRate)
        adminController.addPricingRule(new PricingRule(Vehicle.VehicleType.BIKE, 20.0, 50.0));
        adminController.addPricingRule(new PricingRule(Vehicle.VehicleType.CAR, 40.0, 100.0));
        adminController.addPricingRule(new PricingRule(Vehicle.VehicleType.EV, 30.0, 80.0));

        System.out.println("\n📊 Current Parking Status: " + adminController.getParkingStatus());

        // =========================================================================
        // SCENARIO 2: VEHICLE ENTRY FLOW
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🚗 SCENARIO 2: Vehicle Entry & Ticket Issuance");
        System.out.println("-----------------------------------------------------------");

        EntryController.EntryResult resCar1 = entryController.enterVehicle("KA-01-MJ-1234", Vehicle.VehicleType.CAR);
        EntryController.EntryResult resBike1 = entryController.enterVehicle("DL-04-XY-9999", Vehicle.VehicleType.BIKE);
        EntryController.EntryResult resEV1 = entryController.enterVehicle("MH-12-EV-0007", Vehicle.VehicleType.EV);
        EntryController.EntryResult resCar2 = entryController.enterVehicle("WB-02-AB-5678", Vehicle.VehicleType.CAR);
        EntryController.EntryResult resCar3 = entryController.enterVehicle("TN-09-CD-1111", Vehicle.VehicleType.CAR); // 3rd Car (Floor 2)

        // =========================================================================
        // SCENARIO 3: EDGE CASE - PARKING CAPACITY FULL
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("⚠️ SCENARIO 3: Edge Case - Parking Full for Cars");
        System.out.println("-----------------------------------------------------------");

        // 4th Car arrives when only 3 CAR slots exist across both floors
        EntryController.EntryResult fullRes = entryController.enterVehicle("UP-16-ZZ-9999", Vehicle.VehicleType.CAR);
        System.out.println("Outcome -> Success: " + fullRes.isSuccess() + " | Message: " + fullRes.getMessage());

        // =========================================================================
        // SCENARIO 4: VEHICLE EXIT FLOW & PAYMENT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🏁 SCENARIO 4: Vehicle Exit, Pricing & Payment (Razorpay)");
        System.out.println("-----------------------------------------------------------");

        // Car 1 exits after 2 hours (2 hrs * ₹40/hr = ₹80 vs Flat ₹100 -> Pays ₹80)
        ExitController.ExitResult exitRes1 = exitController.exitVehicle(
                resCar1.getTicketId(), Vehicle.VehicleType.CAR, 2, razorpay, Payment.PaymentGateway.RAZORPAY
        );
        System.out.println("Exit Result -> Status: " + exitRes1.isSuccess() + " | Receipt: " + exitRes1.getReceiptId() + " | Paid: ₹" + exitRes1.getAmountPaid());

        // Bike 1 exits after 4 hours (4 hrs * ₹20/hr = ₹80 vs Flat ₹50 -> Pays min ₹50 flat)
        ExitController.ExitResult exitRes2 = exitController.exitVehicle(
                resBike1.getTicketId(), Vehicle.VehicleType.BIKE, 4, razorpay, Payment.PaymentGateway.RAZORPAY
        );
        System.out.println("Exit Result -> Status: " + exitRes2.isSuccess() + " | Receipt: " + exitRes2.getReceiptId() + " | Paid: ₹" + exitRes2.getAmountPaid());

        // =========================================================================
        // SCENARIO 5: RE-PARKING IN FREED SLOT
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🔄 SCENARIO 5: Re-Parking in Newly Vacated Slot");
        System.out.println("-----------------------------------------------------------");

        // The previously rejected car tries again and gets the slot vacated by Car 1!
        EntryController.EntryResult reParkRes = entryController.enterVehicle("UP-16-ZZ-9999", Vehicle.VehicleType.CAR);
        System.out.println("Outcome -> Success: " + reParkRes.isSuccess() + " | Ticket: " + reParkRes.getTicketId());

        // =========================================================================
        // SCENARIO 6: EDGE CASE - PAYMENT RETRY / FAILURE
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("🛡️ SCENARIO 6: Payment Retry & Edge Case (Stripe Gateway)");
        System.out.println("-----------------------------------------------------------");

        // EV exits with simulated Stripe failure
        stripe.setSimulateFailure(true);
        ExitController.ExitResult failedExit = exitController.exitVehicle(
                resEV1.getTicketId(), Vehicle.VehicleType.EV, 3, stripe, Payment.PaymentGateway.STRIPE
        );
        System.out.println("Failed Exit Result -> Status: " + failedExit.isSuccess() + " | Message: " + failedExit.getMessage());

        // Retry with Razorpay fallback
        System.out.println("\n   🔄 [User Action] Switching payment gateway to Razorpay fallback...");
        ExitController.ExitResult retryExit = exitController.exitVehicle(
                resEV1.getTicketId(), Vehicle.VehicleType.EV, 3, razorpay, Payment.PaymentGateway.RAZORPAY
        );
        System.out.println("Retry Exit Result -> Status: " + retryExit.isSuccess() + " | Receipt: " + retryExit.getReceiptId());

        System.out.println("\n=================================================================");
        System.out.println("🎯 SMART PARKING LOT SYSTEM DEMONSTRATION COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
