package service;

import domain.*;
import domain.state.*;
import repository.DriverRepository;
import repository.RideRepository;
import repository.RiderRepository;
import service.notification.NotificationRouter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RideService {
    private final RideRepository rideRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final PricingService pricingService;
    private final MatchingService matchingService;
    private final PaymentService paymentService;
    private final LocationService locationService;
    private final LockService lockService;
    private final NotificationRouter notificationRouter;

    private final AtomicInteger idCounter = new AtomicInteger(1);

    public RideService(RideRepository rideRepository, RiderRepository riderRepository,
                       DriverRepository driverRepository, PricingService pricingService,
                       MatchingService matchingService, PaymentService paymentService,
                       LocationService locationService, LockService lockService,
                       NotificationRouter notificationRouter) {
        this.rideRepository = rideRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
        this.pricingService = pricingService;
        this.matchingService = matchingService;
        this.paymentService = paymentService;
        this.locationService = locationService;
        this.lockService = lockService;
        this.notificationRouter = notificationRouter;
    }

    public Ride requestRide(RideRequest request) {
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new IllegalArgumentException("Rider " + request.getRiderId() + " not found."));

        FareEstimateResponse estimate = pricingService.calculateFare(request.getPickupLocation(), request.getDropoffLocation());

        int id = idCounter.getAndIncrement();
        String rideId = "RIDE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ride ride = new Ride(id, rideId, rider.getId(), request.getPickupLocation(), request.getDropoffLocation(),
                estimate.getEstimatedFare(), estimate.getEstimatedDistance(), estimate.getEstimatedDuration(), request.getPaymentType());

        if (request.getPaymentType() == PaymentType.PRE_PAYMENT) {
            String paymentId = paymentService.initiatePayment(rideId, estimate.getEstimatedFare(), "MOCK");
            ride.setPaymentId(paymentId);
            ride.setPaymentStatus(PaymentStatus.COMPLETED); // Instant simulation
        }

        rideRepository.save(ride);
        System.out.println("🚕 " + ride + " created.");

        // Async driver matching
        Optional<Driver> matchedDriver = matchingService.matchDriver(ride);
        matchedDriver.ifPresent(driver -> {
            new RequestedState().assign(ride, driver.getId());
            rideRepository.save(ride);
            System.out.println("🎯 Matched and ASSIGNED " + ride.getRideId() + " to Driver " + driver.getName());
        });

        return ride;
    }

    public void driverAccept(String rideId, int driverId) {
        String lockKey = "ride_lock_" + rideId;
        if (!lockService.acquire(lockKey, 500)) {
            throw new IllegalStateException("Could not acquire lock for ride " + rideId);
        }
        try {
            Ride ride = rideRepository.findByRideId(rideId)
                    .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

            new AssignedState().accept(ride, driverId);
            rideRepository.save(ride);

            Rider rider = riderRepository.findById(ride.getRiderId()).orElse(null);
            Driver driver = driverRepository.findById(driverId).orElse(null);

            if (rider != null && driver != null) {
                notificationRouter.send("EMAIL", new NotificationMessage(
                        rider.getEmail(),
                        "Driver En Route",
                        "Driver " + driver.getName() + " accepted your ride in " + driver.getVehicleNumber()
                ));
            }
            System.out.println("✅ Driver " + driverId + " ACCEPTED " + rideId);
        } finally {
            lockService.release(lockKey);
        }
    }

    public void driverDecline(String rideId, int driverId) {
        Ride ride = rideRepository.findByRideId(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

        if (ride.getStatus() == RideStatus.ASSIGNED && ride.getDriverId() != null && ride.getDriverId() == driverId) {
            ride.setDriverId(null);
            ride.setStatus(RideStatus.REQUESTED);
            rideRepository.save(ride);
            System.out.println("⚠️ Driver " + driverId + " DECLINED " + rideId + ". Re-triggering matching...");
            matchingService.matchDriver(ride).ifPresent(nextDriver -> {
                new RequestedState().assign(ride, nextDriver.getId());
                rideRepository.save(ride);
            });
        }
    }

    public void startRide(String rideId, int driverId) {
        Ride ride = rideRepository.findByRideId(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

        new AcceptedState().start(ride, driverId);
        rideRepository.save(ride);
        System.out.println("🚀 " + rideId + " is now IN_PROGRESS.");
    }

    public void completeRide(String rideId, int driverId) {
        Ride ride = rideRepository.findByRideId(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

        new InProgressState().complete(ride, driverId);
        ride.setPaymentStatus(PaymentStatus.COMPLETED);
        rideRepository.save(ride);

        Rider rider = riderRepository.findById(ride.getRiderId()).orElse(null);
        if (rider != null) {
            notificationRouter.send("EMAIL", new NotificationMessage(
                    rider.getEmail(),
                    "Ride Receipt",
                    "Your trip has completed. Total charged: " + ride.getFormattedFare()
            ));
        }
        System.out.println("🏁 " + rideId + " COMPLETED. Receipt issued.");
    }

    public void cancelRide(String rideId, String reason) {
        Ride ride = rideRepository.findByRideId(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        rideRepository.save(ride);
        System.out.println("❌ " + rideId + " CANCELLED. Reason: " + reason);
    }

    public RideStatusResponse getRideStatus(String rideId) {
        Ride ride = rideRepository.findByRideId(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride " + rideId + " not found."));

        Driver driver = (ride.getDriverId() != null) ? driverRepository.findById(ride.getDriverId()).orElse(null) : null;
        Location driverLoc = (driver != null) ? driver.getCurrentLocation() : null;
        Long eta = (driverLoc != null && ride.getStatus() == RideStatus.ACCEPTED) ?
                locationService.calculateETA(driverLoc, ride.getPickupLocation()) : null;

        return new RideStatusResponse(
                ride.getRideId(),
                ride.getStatus(),
                ride.getDriverId(),
                (driver != null ? driver.getName() : null),
                (driver != null ? driver.getVehicleNumber() : null),
                driverLoc,
                eta,
                ride.getEstimatedFare(),
                ride.getPickupLocation(),
                ride.getDropoffLocation(),
                ride.getRequestedAt()
        );
    }
}
