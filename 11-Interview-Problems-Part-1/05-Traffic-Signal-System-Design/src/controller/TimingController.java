package controller;

import domain.Direction;
import domain.SignalTiming;
import service.TimingService;

public class TimingController {
    private final TimingService timingService;

    public TimingController(TimingService timingService) {
        this.timingService = timingService;
    }

    public void setSignalTiming(int intersectionId, Direction direction, int greenDurationSeconds) {
        timingService.setSignalTiming(intersectionId, direction, greenDurationSeconds);
    }

    public void enableDynamicTiming(int intersectionId, Direction direction, boolean enable) {
        timingService.enableDynamicTiming(intersectionId, direction, enable);
    }

    public SignalTiming getSignalTiming(int intersectionId, Direction direction) {
        return timingService.getSignalTiming(intersectionId, direction);
    }

    public void adjustTimingBasedOnTraffic(int intersectionId, Direction direction) {
        timingService.adjustTimingBasedOnTraffic(intersectionId, direction);
    }
}
