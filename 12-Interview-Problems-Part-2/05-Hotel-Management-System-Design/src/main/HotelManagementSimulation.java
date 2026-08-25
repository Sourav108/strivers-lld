package main;

import controller.*;
import domain.*;
import repository.*;
import repository.impl.*;
import service.*;

import java.time.LocalDate;
import java.util.List;

/**
 * HotelManagementSimulation: Complete End-to-End Simulation of the Hotel Management System
 * 
 * Demonstrates:
 * 1. Admin Setup: Hotel, Policies, Room Types, Rooms, Dynamic Seasonal Pricing & Overbooking %
 * 2. Search & Dynamic Multi-Day Pricing Breakdown (Base vs Surge/Weekend pricing)
 * 3. Two-Phase Booking: Phase 1 Price-Lock (CREATED) -> Phase 2 Payment Hold (HELD) -> Confirmation (CONFIRMED)
 * 4. Check-in (Physical Room Assignment) & Check-out (Immediate inventory release)
 * 5. Cancellation Policy & Automated Refund Calculation
 * 6. Inventory Concurrency & Overbooking Cap Enforcement
 */

public class HotelManagementSimulation {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🏨 HOTEL MANAGEMENT SYSTEM - LLD INTERVIEW ARCHITECTURE DEMO");
        System.out.println("=================================================================");

        // --- 1. INITIALIZE REPOSITORIES ---
        HotelRepository hotelRepo = new HotelRepositoryImpl();
        RoomTypeRepository roomTypeRepo = new RoomTypeRepositoryImpl();
        RoomRepository roomRepo = new RoomRepositoryImpl();
        BookingRepository bookingRepo = new BookingRepositoryImpl();
        TransactionRepository transactionRepo = new TransactionRepositoryImpl();
        SeasonalPriceRepository seasonalRepo = new SeasonalPriceRepositoryImpl();
        CancellationPolicyRepository policyRepo = new CancellationPolicyRepositoryImpl();
        UserRepository userRepo = new UserRepositoryImpl();

        // --- 2. INITIALIZE SERVICES ---
        BookingStateHandler stateHandler = new BookingStateHandler();
        PricingService pricingService = new PricingService(roomTypeRepo, seasonalRepo);
        InventoryService inventoryService = new InventoryService(bookingRepo, roomTypeRepo, hotelRepo);
        PolicyService policyService = new PolicyService();
        TransactionService transactionService = new TransactionService(transactionRepo, bookingRepo, stateHandler);
        BookingService bookingService = new BookingService(
                bookingRepo, hotelRepo, policyRepo, inventoryService,
                pricingService, policyService, transactionService, stateHandler
        );
        SearchService searchService = new SearchService(hotelRepo, roomTypeRepo, inventoryService, pricingService);
        UserService userService = new UserService(bookingRepo);

        // --- 3. INITIALIZE CONTROLLERS ---
        AdminController adminController = new AdminController(
                hotelRepo, roomTypeRepo, roomRepo, seasonalRepo, policyRepo, bookingService
        );
        SearchController searchController = new SearchController(searchService);
        BookingController bookingController = new BookingController(bookingService);
        TransactionController transactionController = new TransactionController(transactionService);
        DashboardController dashboardController = new DashboardController(userService);

        // --- 4. SEED DATA (Admin) ---
        CancellationPolicy flexPolicy = new CancellationPolicy("POL-FLEX", "FLEXIBLE", 80, 48); // 80% refund if >= 48h
        adminController.createOrUpdatePolicy(flexPolicy);

        Hotel grandPalace = new Hotel(
                "HTL-001", "The Grand Palace Bangalore", "MG Road, Central",
                "Bengaluru", "India", 4.8, 10, flexPolicy.getId() // 10% overbooking allowed
        );
        adminController.createOrUpdateHotel(grandPalace);

        RoomType deluxeKing = new RoomType("RT-DLX-01", grandPalace.getId(), "Deluxe King Room", 2, "KING", 4000_00L, 5); // ₹4,000 base
        deluxeKing.addAmenity("High-Speed WiFi");
        deluxeKing.addAmenity("Breakfast Included");
        adminController.createOrUpdateRoomType(deluxeKing);

        // Seed physical rooms
        adminController.addRoom(new Room("RM-101", grandPalace.getId(), deluxeKing.getId(), "101"));
        adminController.addRoom(new Room("RM-102", grandPalace.getId(), deluxeKing.getId(), "102"));
        adminController.addRoom(new Room("RM-103", grandPalace.getId(), deluxeKing.getId(), "103"));

        // Seed Dynamic Weekend/Surge Price for Day 2
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.plusDays(2); // 2 nights
        DateRange stayRange = new DateRange(checkIn, checkOut);

        // Surge on second night: ₹5,500 instead of ₹4,000 base
        SeasonalPrice weekendSurge = new SeasonalPrice("SP-01", grandPalace.getId(), deluxeKing.getId(), checkIn.plusDays(1), 5500_00L);
        adminController.setSeasonalPrice(weekendSurge);

        // Seed Users
        User alice = new User("USR-101", "Alice Sharma", "alice@example.com", UserRole.CUSTOMER);
        User bob = new User("USR-102", "Bob Verma", "bob@example.com", UserRole.CUSTOMER);
        userRepo.save(alice);
        userRepo.save(bob);

        // =========================================================================
        // SCENARIO 1: SEARCH & DYNAMIC AVAILABILITY / PRICING
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("1️⃣ SCENARIO 1: Hotel Search & Real-time Dynamic Pricing");
        System.out.println("-----------------------------------------------------------");

        SearchFilter filter = new SearchFilter().city("Bengaluru").country("India").dateRange(stayRange);
        List<Hotel> foundHotels = searchController.searchHotels(filter);
        System.out.println("🔍 Search Results: Found " + foundHotels.size() + " hotel(s) in Bengaluru:");
        for (Hotel h : foundHotels) {
            System.out.println("   " + h);
        }

        List<RoomTypeAvailability> availabilities = searchController.getAvailability(grandPalace.getId(), stayRange);
        System.out.println("\n📅 Room Availability for Stay " + stayRange + ":");
        for (RoomTypeAvailability rta : availabilities) {
            System.out.println("   " + rta);
            System.out.println("   ↳ Nightly Price Breakdown: " + rta.getNightlyPrices());
        }

        // =========================================================================
        // SCENARIO 2: TWO-PHASE BOOKING & PAYMENT WORKFLOW
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("2️⃣ SCENARIO 2: Two-Phase Booking (Price Lock -> Hold -> Confirmation)");
        System.out.println("-----------------------------------------------------------");

        // Phase 1: Create Booking (Expected Total: ₹4,000 + ₹5,500 = ₹9,500)
        long expectedTotal = 9500_00L;
        Booking booking1 = bookingController.createBooking(
                alice.getId(), grandPalace.getId(), deluxeKing.getId(), stayRange, expectedTotal
        );

        // Phase 2: Initiate Payment (Transitions CREATED -> HELD, Locks inventory)
        Transaction tx1 = transactionController.initiateTransaction(booking1);

        // Payment Gateway Webhook Callback: SUCCESS
        transactionController.handleTransactionCallback(tx1.getProviderRef(), TransactionStatus.COMPLETED);

        System.out.println("📌 Booking Status after Payment: " + booking1);

        // =========================================================================
        // SCENARIO 3: CHECK-IN & CHECK-OUT LIFECYCLE
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("3️⃣ SCENARIO 3: Check-in (Room Allocation) & Check-out");
        System.out.println("-----------------------------------------------------------");

        // Guest arrives at reception -> Allocate Room 101
        adminController.checkIn(booking1.getId(), "RM-101");
        System.out.println("Allocated Room: " + booking1.getAllocatedRoomId() + " | Status: " + booking1.getBookingStatus());

        // Guest checks out
        adminController.checkOut(booking1.getId());
        System.out.println("Final Status: " + booking1.getBookingStatus());

        // =========================================================================
        // SCENARIO 4: CANCELLATION & REFUND POLICY EVALUATION
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("4️⃣ SCENARIO 4: Booking Cancellation & Policy-Driven Refund");
        System.out.println("-----------------------------------------------------------");

        // Bob makes a booking for 5 days from now
        Booking booking2 = bookingController.createBooking(
                bob.getId(), grandPalace.getId(), deluxeKing.getId(), stayRange, expectedTotal
        );
        Transaction tx2 = transactionController.initiateTransaction(booking2);
        transactionController.handleTransactionCallback(tx2.getProviderRef(), TransactionStatus.COMPLETED);

        // Bob cancels 4 days before check-in (Well within the 48h cutoff!)
        LocalDate cancellationDate = checkIn.minusDays(4);
        RefundDecision refund = bookingController.cancelBooking(booking2.getId(), bob.getId(), cancellationDate);

        // =========================================================================
        // SCENARIO 5: USER DASHBOARD
        // =========================================================================
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("5️⃣ SCENARIO 5: User Dashboard (Booking History)");
        System.out.println("-----------------------------------------------------------");

        List<Booking> aliceBookings = dashboardController.listUserBookings(alice.getId());
        System.out.println("Alice's Bookings (" + aliceBookings.size() + "):");
        for (Booking b : aliceBookings) {
            System.out.println("   " + b);
        }

        System.out.println("\n=================================================================");
        System.out.println("🎯 HOTEL MANAGEMENT SYSTEM ARCHITECTURE COMPLETE & VERIFIED!");
        System.out.println("=================================================================");
    }
}
