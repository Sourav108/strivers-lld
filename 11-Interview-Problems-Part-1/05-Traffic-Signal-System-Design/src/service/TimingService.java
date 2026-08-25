package service;

import domain.Direction;
import domain.Intersection;
import domain.SignalTiming;
import repository.TimingRepository;

public class TimingService {
    private final IntersectionService intersectionService;
    private final TimingRepository timingRepository;

    public TimingService(IntersectionService intersectionService, TimingRepository timingRepository) {
        this.intersectionService = intersectionService;
        this.timingRepository = timingRepository;
    }

    public void setSignalTiming(int intersectionId, Direction direction, int greenDurationSeconds) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        SignalTiming timing = intersection.getSignalTiming(direction);
        timing.setGreenDurationSeconds(greenDurationSeconds);
        timingRepository.save(intersectionId, timing);
        System.out.println("⏱️ [Timing Config] Set " + direction + " green duration to " + greenDurationSeconds + "s.");
    }

    public void enableDynamicTiming(int intersectionId, Direction direction, boolean enable) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        intersection.getSignalTiming(direction).setDynamic(enable);
    }

    public SignalTiming getSignalTiming(int intersectionId, Direction direction) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        return intersection.getSignalTiming(direction);
    }

    public int calculateOptimalGreenDuration(int vehicleCount) {
        if (vehicleCount < 5) return 10;
        if (vehicleCount <= 20) return 25;
        return 45; // Heavy traffic congestion
    }

    public void adjustTimingBasedOnTraffic(int intersectionId, Direction direction) {
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        SignalTiming timing = intersection.getSignalTiming(direction);
        if (timing.isDynamic()) {
            int vehicleCount = intersection.getVehicleCounter(direction).getCount();
            int optimalGreen = calculateOptimalGreenDuration(vehicleCount);
            timing.setGreenDurationSeconds(optimalGreen);
            timingRepository.save(intersectionId, timing);
            System.out.println("⚡ [Dynamic Timing Adjustment] " + direction + " (" + vehicleCount + " cars) -> Adjusted Green Duration: " + optimalGreen + "s.");
        }
    }
}
