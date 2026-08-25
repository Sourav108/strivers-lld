package domain;

import domain.state.RedState;
import domain.state.TrafficLightState;

public class TrafficLight {
    private final Direction direction;
    private TrafficLightState currentState;

    public TrafficLight(Direction direction) {
        this.direction = direction;
        this.currentState = new RedState(); // Default safe state is RED
    }

    public Direction getDirection() { return direction; }
    public TrafficLightState getState() { return currentState; }
    public String getStateName() { return currentState.getStateName(); }

    public void setState(TrafficLightState state) {
        this.currentState = state;
    }

    public void turnGreen() {
        currentState.turnGreen(this);
    }

    public void turnYellow() {
        currentState.turnYellow(this);
    }

    public void turnRed() {
        currentState.turnRed(this);
    }

    public void turnOff() {
        currentState.turnOff(this);
    }

    // Emergency transition following safe state sequence (GREEN -> YELLOW -> RED)
    public void emergencyTransitionToRed() {
        if ("GREEN".equals(currentState.getStateName())) {
            turnYellow();
            turnRed();
        } else if ("YELLOW".equals(currentState.getStateName())) {
            turnRed();
        }
    }

    @Override
    public String toString() {
        return "[" + direction + ": " + currentState.getStateName() + "]";
    }
}
