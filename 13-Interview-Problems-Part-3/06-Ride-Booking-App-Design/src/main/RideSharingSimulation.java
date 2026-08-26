package main;

import controller.DriverController;
import controller.PaymentController;
import controller.RideController;
import domain.*;
import domain.strategy.PaymentGatewayRouter;
import domain.strategy.RazorpayPaymentGatewayProvider;
import domain.strategy.StripePaymentGatewayProvider;
import repository.impl.DriverRepositoryImpl;
import repository.impl.LocationRepositoryImpl;
import repository.impl.RideRepositoryImpl;
import repository.impl.RiderRepositoryImpl;
import service.*;
import service.notification.NotificationRouter;

/**
 * Driver simulation for the Ride Booking System LLD.
 * Demonstrates:
 * 1. User & Driver onboarding, online/offline toggles, GPS telemetry
 * 2. Upfront fare estimation & surge pricing
 * 3. Pre-paid & post-paid ride booking
 * 4. Async driver matching & accept/decline workflows
 * 5. Trip lifecycle (REQUESTED -> ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED)
 * 6. Edge cases: Driver double-assignment protection, ride cancellation, receipt generation
 */
public class RideSharingSimulation {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🚗 RIDE BOOKING SYSTEM (UBER/LYFT) - LLD DEMO");
        System.out.println("==================================================");

        // 1. Dependency Injection & Infrastructure Setup
        RiderRepositoryImpl riderRepo = new RiderRepositoryImpl();
        DriverRepositoryImpl driverRepo = new DriverRepositoryImpl();
        RideRepositoryImpl rideRepo = new RideRepositoryImpl();
        LocationRepositoryImpl locationRepo = new LocationRepositoryImpl();

        NotificationRouter notificationRouter = new NotificationRouter();
        LockService lockService = new LockService();
        LocationService locationService = new LocationService(locationRepo);
        PricingService pricingService = new PricingService(locationService);

        PaymentGatewayRouter paymentRouter = new PaymentGatewayRouter();
        paymentRouter.registerProvider(new StripePaymentGatewayProvider());
        paymentRouter.registerProvider(new RazorpayPaymentGatewayProvider());
        PaymentService paymentService = new PaymentService(paymentRouter);

        DriverService driverService = new DriverService(driverRepo, locationService);
        MatchingService matchingService = new MatchingService(driverService, lockService, notificationRouter);
        RideService rideService = new RideService(rideRepo, riderRepo, driverRepo, pricingService,
                matchingService, paymentService, locationService, lockService, notificationRouter);

        RideController rideController = new RideController(rideService, pricingService);
        DriverController driverController = new DriverController(driverService, rideService);
        PaymentController paymentController = new PaymentController(paymentService);

        // 2. Rider & Driver Onboarding
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 1. ONBOARDING RIDERS & DRIVERS");
        System.out.println("--------------------------------------------------");
        Rider alice = new Rider(1, "alice", "alice@example.com", "+1-555-0101", "Alice Smith");
        Rider bob = new Rider(2, "bob", "bob@example.com", "+1-555-0102", "Bob Johnson");
        riderRepo.save(alice);
        riderRepo.save(bob);
        System.out.println("👤 Registered: " + alice);
        System.out.println("👤 Registered: " + bob);

        Driver dave = new Driver(101, "dave", "dave@example.com", "+1-555-0201", "Dave Miller",
                "DL-1001", "KA-01-AB-1234", "Sedan");
        Driver dan = new Driver(102, "dan", "dan@example.com", "+1-555-0202", "Dan Wilson",
                "DL-1002", "KA-02-CD-5678", "SUV");
        driverRepo.save(dave);
        driverRepo.save(dan);
        System.out.println("🚙 Registered: " + dave);
        System.out.println("🚙 Registered: " + dan);

        // Drivers go online with locations
        driverController.goOnline(dave.getId());
        driverController.updateLocation(dave.getId(), new Location(12.9720, 77.5950, "Brigade Road"));

        driverController.goOnline(dan.getId());
        driverController.updateLocation(dan.getId(), new Location(12.9850, 77.6050, "MG Road"));

        // 3. Upfront Fare Estimate
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 2. UPFRONT FARE ESTIMATION");
        System.out.println("--------------------------------------------------");
        Location pickup = new Location(12.9716, 77.5946, "MG Road Metro Station");
        Location dropoff = new Location(13.0358, 77.5970, "Hebbal Flyover");

        FareEstimateResponse estimate = rideController.getFareEstimate(pickup, dropoff);
        System.out.println("💰 " + estimate);

        // 4. Request Ride (Pre-Payment Flow)
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 3. RIDE REQUEST & ASYNC DRIVER MATCHING");
        System.out.println("--------------------------------------------------");
        RideRequest rideRequest = new RideRequest(alice.getId(), pickup, dropoff, PaymentType.PRE_PAYMENT);
        Ride aliceRide = rideController.requestRide(rideRequest);

        // 5. Driver Accepts Ride
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 4. DRIVER ACCEPTANCE & RIDE TRACKING");
        System.out.println("--------------------------------------------------");
        driverController.acceptRide(aliceRide.getRideId(), dave.getId());

        RideStatusResponse statusResponse = rideController.getRideStatus(aliceRide.getRideId());
        System.out.println("📱 Rider Polling Status: " + statusResponse);

        // 6. Driver GPS Updates & Trip Progression
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 5. TRIP EXECUTION & TELEMETRY");
        System.out.println("--------------------------------------------------");
        // Driver arrives at pickup
        driverController.updateLocation(dave.getId(), pickup);
        // Driver starts trip
        driverController.startRide(aliceRide.getRideId(), dave.getId());

        // Driver navigates to dropoff
        driverController.updateLocation(dave.getId(), dropoff);
        // Driver completes trip
        driverController.completeRide(aliceRide.getRideId(), dave.getId());

        // 7. Ride Cancellation Scenario
        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 6. RIDE CANCELLATION WORKFLOW");
        System.out.println("--------------------------------------------------");
        RideRequest bobRequest = new RideRequest(bob.getId(), pickup, dropoff, PaymentType.POST_PAYMENT);
        Ride bobRide = rideController.requestRide(bobRequest);
        rideController.cancelRide(bobRide.getRideId(), "Rider changed plans");

        System.out.println("\n==================================================");
        System.out.println("✅ RIDE BOOKING SIMULATION COMPLETED SUCCESSFULLY");
        System.out.println("==================================================");
    }
}
