package domain.state;

import domain.TrafficLight;

public class RedState implements TrafficLightState {
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        trafficLight.setState(new GreenState());
    }

    @Override
    public void turnYellow(TrafficLight trafficLight) {
        throw new InvalidStateTransitionException("❌ Invalid transition: Cannot change directly from RED to YELLOW.");
    }

    @Override
    public void turnRed(TrafficLight trafficLight) {
        // Already RED, no-op
    }

    @Override
    public void turnOff(TrafficLight trafficLight) {
        trafficLight.setState(new OffState());
    }

    @Override
    public String getStateName() {
        return "RED";
    }
}
