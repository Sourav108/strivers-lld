package controller;

import domain.Direction;
import domain.Intersection;
import service.IntersectionService;

public class IntersectionController {
    private final IntersectionService intersectionService;

    public IntersectionController(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
    }

    public void createIntersection(int id, String name) {
        intersectionService.createIntersection(id, name);
    }

    public Intersection getIntersection(int id) {
        return intersectionService.getIntersection(id);
    }

    public void advancePhase(int id, Direction greenDirection) {
        intersectionService.advancePhase(id, greenDirection);
    }

    public void displayStatus(int id) {
        intersectionService.displayStatus(id);
    }
}
