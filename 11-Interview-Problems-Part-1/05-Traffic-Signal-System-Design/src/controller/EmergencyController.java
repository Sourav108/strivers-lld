package controller;

import domain.Direction;
import domain.EmergencyRequest;
import service.EmergencyService;

public class EmergencyController {
    private final EmergencyService emergencyService;

    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    public void requestEmergency(int intersectionId, Direction direction, int durationSeconds) {
        emergencyService.requestEmergency(intersectionId, direction, durationSeconds);
    }

    public void endEmergency(int intersectionId) {
        emergencyService.endEmergency(intersectionId);
    }

    public void getEmergencyStatus(int intersectionId) {
        EmergencyRequest activeEmergency = emergencyService.getActiveEmergency(intersectionId);
        if (activeEmergency != null) {
            System.out.println("=== Emergency Status ===");
            System.out.println("Emergency ID: " + activeEmergency.getId());
            System.out.println("Direction: " + activeEmergency.getEmergencyDirection());
            System.out.println("Duration: " + activeEmergency.getDurationSeconds() + "s");
            System.out.println("Active: " + activeEmergency.isActive());
        } else {
            System.out.println("No active emergency for Intersection #" + intersectionId);
        }
    }
}
